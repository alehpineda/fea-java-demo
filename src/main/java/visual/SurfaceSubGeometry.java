package visual;

import elem.*;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.ListIterator;

/**
 * Produces JavaFX 3D geometry nodes for the FE model surface.
 *
 * <p>Extends {@link SurfaceGeometry} which already identifies which
 * faces, edges and nodes lie on the outer surface and centres the mesh.
 * This class adds quadratic subdivision of each face into small
 * triangles and converts the resulting float arrays into JavaFX
 * {@link MeshView}, edge-cylinder {@link Group} and node-sphere
 * {@link Group} objects.</p>
 */
class SurfaceSubGeometry extends SurfaceGeometry {

    /** Total number of triangle vertices assembled for the surface mesh. */
    int nVertices;
    private FaceSubdivision fs;
    /** Per-face edge subdivision counts {@code [nFaces][4]}. */
    private int[][] edgeDiv;

    /** Flattened xyz coordinates for the surface triangle array (3 floats/vertex). */
    private float[] xyzSurface;
    /** Flattened per-vertex normals (3 floats/vertex). */
    private float[] norSurface;
    /** Texture coordinates for contour colouring (2 floats/vertex: u, v=0). */
    private float[] texSurface;

    // Working arrays for one element face
    private double[][] xyzFace  = new double[8][3];
    private double[]   funFace  = new double[8];
    private double[]   an       = new double[8];
    private double[][] deriv    = new double[8][2];
    private double[]   xyzFacePoints;
    private double[]   norFacePoints;
    private double[]   texFacePoints;

    /**
     * Constructs the subdivision by calling the parent constructor
     * (which identifies surface geometry) and then triangulating every
     * surface face.
     */
    SurfaceSubGeometry() {

        super();

        fs = new FaceSubdivision();
        edgeDiv = new int[nFaces][4];

        int np = (VisData.nDivMax + 1) * (VisData.nDivMax + 1);
        xyzFacePoints = new double[3 * np];
        norFacePoints = new double[3 * np];
        texFacePoints = new double[np];

        int nTrigs = setEdgeDivisions();

        xyzSurface = new float[9 * nTrigs];
        norSurface = new float[9 * nTrigs];
        texSurface = new float[6 * nTrigs];

        setModelTriangles();
    }

    /**
     * Computes the number of edge subdivisions for every surface face
     * and returns an upper estimate of the total number of triangles.
     */
    int setEdgeDivisions() {

        int nTriangles = 0;
        ListIterator<int[]> f = listFaces.listIterator(0);

        for (int face = 0; face < nFaces; face++) {
            setFaceCoordFun(f.next());
            for (int i = 0; i < 4; i++) {
                int nd = fs.numberOfEdgeDivisions(xyzFace,
                        funFace, deltaf, VisData.drawContours,
                        2 * i, 2 * i + 1, (2 * i + 2) % 8);
                edgeDiv[face][i] = nd;
                nTriangles += (int) (0.6 * nd * nd + 2);
            }
        }
        return nTriangles;
    }

    /** Copies coordinates and result values for the eight face nodes. */
    void setFaceCoordFun(int[] faceNodes) {

        for (int i = 0; i < faceNodes.length; i++) {
            int ind = faceNodes[i] - 1;
            for (int j = 0; j < 3; j++) {
                if (fem.nDim == 2 && j == 2) xyzFace[i][j] = 0;
                else xyzFace[i][j] = fem.getNodeCoord(ind, j);
            }
            if (VisData.drawContours) funFace[i] = fun[ind];
        }
    }

    /** Performs Delaunay triangulation for all surface faces. */
    void setModelTriangles() {

        nVertices = 0;
        ListIterator<int[]> f = listFaces.listIterator(0);

        for (int face = 0; face < nFaces; face++) {
            int[] faceNodes = f.next();
            setFaceCoordFun(faceNodes);
            fs.subdivideFace(edgeDiv[face]);
            setFaceVertices(faceNodes);

            for (int t = 0; t < fs.nTrigs; t++) {
                for (int k = 0; k < 3; k++) {
                    int ind = fs.trigs[t][k];
                    for (int i = 0; i < 3; i++) {
                        xyzSurface[3 * nVertices + i] = (float) xyzFacePoints[3 * ind + i];
                        norSurface[3 * nVertices + i] = (float) norFacePoints[3 * ind + i];
                    }
                    texSurface[2 * nVertices]     = (float) texFacePoints[ind];
                    texSurface[2 * nVertices + 1] = 0.5f;
                    nVertices++;
                }
            }
        }
    }

    /** Interpolates global coordinates, normals and texture coords for face vertices. */
    private void setFaceVertices(int[] faceNodes) {

        double[][] e  = new double[2][3];
        double[]   en = new double[3];

        for (int iv = 0; iv < fs.nFacePoints; iv++) {
            ShapeQuad3D.shapeDerivFace(fs.xi[iv], fs.et[iv],
                    faceNodes, an, deriv);
            for (int j = 0; j < 3; j++) {
                double s = 0;
                for (int i = 0; i < 8; i++) s += an[i] * xyzFace[i][j];
                xyzFacePoints[3 * iv + j] = s;
            }
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 3; j++) {
                    double s = 0;
                    for (int k = 0; k < 8; k++) s += deriv[k][i] * xyzFace[k][j];
                    e[i][j] = s;
                }
            }
            en[0] = e[0][1] * e[1][2] - e[1][1] * e[0][2];
            en[1] = e[0][2] * e[1][0] - e[1][2] * e[0][0];
            en[2] = e[0][0] * e[1][1] - e[1][0] * e[0][1];
            double s = 1.0 / Math.sqrt(en[0] * en[0] + en[1] * en[1] + en[2] * en[2]);
            for (int i = 0; i < 3; i++) norFacePoints[3 * iv + i] = en[i] * s;

            if (VisData.drawContours) {
                double fv = 0;
                for (int i = 0; i < 8; i++) fv += an[i] * funFace[i];
                double t = (fv - fmin) / (fmax - fmin);
                if (t < 0.003) t = 0.003;
                if (t > 0.997) t = 0.997;
                texFacePoints[iv] = (float) t;
            }
        }
    }

    /**
     * Builds a {@link MeshView} from the triangulated surface.
     *
     * <p>The {@link TriangleMesh} uses per-vertex texture coordinates so
     * that contour colours (from {@link ColorScale}) are mapped correctly.
     * When contours are disabled the texture coordinates are set to a
     * neutral centre value and no diffuse map is applied.</p>
     *
     * @return a ready-to-add {@link MeshView}
     */
    MeshView getModelMesh() {

        TriangleMesh mesh = new TriangleMesh(VertexFormat.POINT_TEXCOORD);

        // Points: 3 floats per vertex
        mesh.getPoints().setAll(xyzSurface, 0, nVertices * 3);

        // TexCoords: 2 floats per vertex (u, v)
        if (VisData.drawContours) {
            mesh.getTexCoords().setAll(texSurface, 0, nVertices * 2);
        } else {
            float[] dummy = new float[nVertices * 2];
            for (int i = 0; i < nVertices; i++) {
                dummy[2 * i]     = 0.5f;
                dummy[2 * i + 1] = 0.5f;
            }
            mesh.getTexCoords().setAll(dummy);
        }

        // Faces: 6 ints per triangle [p0 t0 p1 t1 p2 t2]
        int nTrigs = nVertices / 3;
        int[] faces = new int[nTrigs * 6];
        for (int t = 0; t < nTrigs; t++) {
            for (int k = 0; k < 3; k++) {
                int v = t * 3 + k;
                faces[t * 6 + k * 2]     = v;
                faces[t * 6 + k * 2 + 1] = v;
            }
        }
        mesh.getFaces().setAll(faces);

        MeshView view = new MeshView(mesh);
        view.setCullFace(CullFace.BACK);
        return view;
    }

    /**
     * Builds a {@link Group} of thin cylinders representing the mesh edges.
     *
     * <p>JavaFX 3D has no native line primitive; cylinders oriented
     * between each pair of segment endpoints are the standard workaround.
     * The cylinder radius is 0.3 % of the model's largest dimension.</p>
     *
     * @return group containing one {@link Cylinder} per line segment
     */
    Group getModelEdges() {

        Group group = new Group();
        double radius = Math.max(sizeMax * 0.003, 1e-6);

        PhongMaterial mat = new PhongMaterial(VisData.edgeColor);

        double[][] xys = new double[3][3];
        double[] segAn = new double[3];

        ListIterator<int[]> e = listEdges.listIterator(0);

        for (int edge = 0; edge < nEdges; edge++) {
            int[] edgeNodes = e.next();
            for (int k = 0; k < 3; k++) {
                for (int n = 0; n < fem.nDim; n++) {
                    xys[k][n] = fem.getNodeCoord(edgeNodes[k] - 1, n);
                }
            }

            int ndiv = fs.numberOfEdgeDivisions(xys, null,
                    deltaf, false, 0, 1, 2);
            double dxi = 2.0 / ndiv;

            double ax = xys[0][0], ay = xys[0][1], az = (fem.nDim > 2) ? xys[0][2] : 0;

            for (int k = 1; k <= ndiv; k++) {
                double xi = -1 + k * dxi;
                segAn[0] = -0.5 * xi * (1 - xi);
                segAn[1] =  1 - xi * xi;
                segAn[2] =  0.5 * xi * (1 + xi);

                double bx = 0, by = 0, bz = 0;
                for (int j = 0; j < 3; j++) {
                    bx += xys[j][0] * segAn[j];
                    by += xys[j][1] * segAn[j];
                    bz += (fem.nDim > 2) ? xys[j][2] * segAn[j] : 0;
                }

                Node seg = makeCylinder(ax, ay, az, bx, by, bz, radius, mat);
                group.getChildren().add(seg);

                ax = bx;
                ay = by;
                az = bz;
            }
        }
        return group;
    }

    /**
     * Builds a {@link Group} of small spheres marking every surface node.
     *
     * @return group containing one {@link Sphere} per surface node
     */
    Group getModelNodes() {

        Group group = new Group();
        double radius = Math.max(sizeMax * 0.006, 1e-6);
        PhongMaterial mat = new PhongMaterial(VisData.nodeColor);

        for (int node = 0; node < sNodes.length; node++) {
            if (sNodes[node] > 0) {
                double x = fem.getNodeCoord(node, 0);
                double y = fem.nDim > 1 ? fem.getNodeCoord(node, 1) : 0;
                double z = fem.nDim > 2 ? fem.getNodeCoord(node, 2) : 0;
                Sphere sphere = new Sphere(radius);
                sphere.setMaterial(mat);
                sphere.setTranslateX(x);
                sphere.setTranslateY(y);
                sphere.setTranslateZ(z);
                group.getChildren().add(sphere);
            }
        }
        return group;
    }

    /**
     * Creates a thin {@link Cylinder} connecting two 3-D points.
     * JavaFX cylinders are centred at the origin and aligned with the
     * Y axis; we translate to the midpoint and rotate to the segment
     * direction.
     */
    private static javafx.scene.Node makeCylinder(
            double ax, double ay, double az,
            double bx, double by, double bz,
            double radius, PhongMaterial mat) {

        double dx = bx - ax, dy = by - ay, dz = bz - az;
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length < 1e-12) return new Group();

        Cylinder cyl = new Cylinder(radius, length);
        cyl.setMaterial(mat);

        // Midpoint
        double mx = 0.5 * (ax + bx);
        double my = 0.5 * (ay + by);
        double mz = 0.5 * (az + bz);

        // Rotate the default Y-axis cylinder to direction (dx,dy,dz)
        Point3D yAxis = Rotate.Y_AXIS;
        Point3D dir   = new Point3D(dx / length, dy / length, dz / length);
        Point3D axis  = yAxis.crossProduct(dir);
        double  angle = Math.toDegrees(Math.acos(
                            Math.max(-1.0, Math.min(1.0, yAxis.dotProduct(dir)))));

        Group g = new Group(cyl);
        g.getTransforms().addAll(
            new Translate(mx, my, mz),
            axis.magnitude() < 1e-10
                ? new Rotate(angle < 90 ? 0 : 180, Rotate.X_AXIS)
                : new Rotate(angle, axis)
        );
        return g;
    }
}
