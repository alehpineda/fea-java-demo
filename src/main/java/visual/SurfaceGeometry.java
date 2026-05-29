package visual;

import model.*;

import java.util.*;

/**
 * Computes surface geometry (faces, edges, nodes) of the FE model.
 *
 * <p>Walks the element connectivity to identify mesh faces that lie on
 * the outer surface, derives edges from those faces, and marks the
 * corresponding nodes.  Applies optional deformation scaling and
 * centres the model at the origin for camera alignment.</p>
 */
class SurfaceGeometry {

    FeModel fem;

    /** Number of surface faces, edges and nodes. */
    int nFaces, nEdges, nsNodes;
    /** Connectivity lists for surface faces and edges. */
    LinkedList<int[]> listFaces;
    LinkedList<int[]> listEdges;
    /** sNodes[i] > 0 if node i is on the surface. */
    int[] sNodes;

    double[] fun;
    double fmin, fmax, deltaf;
    private static double[] xyzmin = new double[3];
    private static double[] xyzmax = new double[3];
    /** Largest dimension of the bounding box (used for scaling). */
    static double sizeMax;

    SurfaceGeometry() {

        fem = VisData.fem;
        listFaces = new LinkedList<>();
        listEdges = new LinkedList<>();
        sNodes = new int[fem.nNod];

        createFaces();
        nFaces = listFaces.size();

        createEdges();
        nEdges = listEdges.size();

        createNodes();

        if (VisData.drawContours) {
            fun = new double[fem.nNod];
            ResultAtNodes ran = new ResultAtNodes(this, fem);
            ran.setParmAtNodes(VisData.parm, VisData.displ);
        }

        modifyNodeCoordinates();
    }

    /**
     * Populates {@link #listFaces} with element faces on the outer surface.
     * For 2-D models every element face is a surface face.
     */
    void createFaces() {

        if (fem.nDim == 3) {
            for (int iel = 0; iel < fem.nEl; iel++) {
                int[][] elemFaces = fem.elems[iel].getElemFaces();
                for (int[] elemFace : elemFaces) {
                    int nNodes = elemFace.length;
                    int[] faceNodes = new int[nNodes];
                    for (int i = 0; i < nNodes; i++) {
                        faceNodes[i] = fem.elems[iel].ind[elemFace[i]];
                    }
                    // Skip degenerated 8-node face
                    if (nNodes == 8 &&
                            (faceNodes[3] == faceNodes[7] ||
                             faceNodes[1] == faceNodes[5])) {
                        continue;
                    }
                    ListIterator<int[]> f = listFaces.listIterator(0);
                    boolean faceFound = false;
                    while (f.hasNext()) {
                        int[] faceNodesA = f.next();
                        if (equalFaces(faceNodes, faceNodesA)) {
                            f.remove();
                            faceFound = true;
                            break;
                        }
                    }
                    if (!faceFound) {
                        listFaces.addLast(faceNodes);
                    }
                }
            }
        } else {
            for (int iel = 0; iel < fem.nEl; iel++) {
                listFaces.addLast(fem.elems[iel].ind);
            }
        }
    }

    /**
     * Returns {@code true} when the two face connectivity arrays share
     * the same corner nodes (ignoring mid-side nodes and ordering).
     */
    boolean equalFaces(int[] f1, int[] f2) {

        int step = (f1.length > 4) ? 2 : 1;
        for (int j = 0; j < f1.length; j += step) {
            int n1 = f1[j];
            boolean nodeFound = false;
            for (int i = 0; i < f2.length; i += step) {
                if (f2[i] == n1) {
                    nodeFound = true;
                    break;
                }
            }
            if (!nodeFound) return false;
        }
        return true;
    }

    /**
     * Populates {@link #listEdges} with unique surface edges derived
     * from {@link #listFaces}.
     */
    void createEdges() {

        for (int iFace = 0; iFace < nFaces; iFace++) {

            int[] faceNodes = listFaces.get(iFace);
            int nFaceNodes = faceNodes.length;
            int step = (nFaceNodes > 4) ? 2 : 1;

            for (int inod = 0; inod < nFaceNodes; inod += step) {
                int[] edgeNodes = new int[step + 1];
                for (int i = inod, k = 0; i <= inod + step; i++, k++) {
                    edgeNodes[k] = faceNodes[i % nFaceNodes];
                }
                ListIterator<int[]> ea = listEdges.listIterator(0);
                boolean edgeFound = false;
                while (ea.hasNext()) {
                    int[] edgeNodesA = ea.next();
                    if (equalEdges(edgeNodes, edgeNodesA)) {
                        edgeFound = true;
                        break;
                    }
                }
                if (!edgeFound) {
                    listEdges.addLast(edgeNodes);
                }
            }
        }
    }

    /**
     * Returns {@code true} when two edge arrays share the same end
     * nodes (direction-independent comparison).
     */
    boolean equalEdges(int[] e1, int[] e2) {

        int len = e1.length - 1;
        return (e1[0] == e2[0] && e1[len] == e2[len]) ||
               (e1[0] == e2[len] && e1[len] == e2[0]);
    }

    /**
     * Marks surface nodes: {@code sNodes[i] = 1} for every node that
     * appears in at least one surface edge.
     */
    void createNodes() {

        Arrays.fill(sNodes, 0);
        ListIterator<int[]> e = listEdges.listIterator();
        for (int iEdge = 0; iEdge < nEdges; iEdge++) {
            int[] edgeNodes = e.next();
            for (int node : edgeNodes) {
                sNodes[node - 1] = 1;
            }
        }
        nsNodes = 0;
        for (int s : sNodes) {
            if (s > 0) nsNodes++;
        }
    }

    /**
     * Optionally adds scaled displacements to node coordinates (deformed
     * shape), then centres the model so the bounding-box midpoint is at
     * the origin.
     */
    void modifyNodeCoordinates() {

        if (VisData.showDeformShape) {
            setBoundingBox();
            double displMax = 0;
            for (int i = 0; i < fem.nNod; i++) {
                double d = 0;
                for (int j = 0; j < fem.nDim; j++) {
                    double s = VisData.displ[i * fem.nDim + j];
                    d += s * s;
                }
                displMax = Math.max(d, displMax);
            }
            displMax = Math.sqrt(displMax);
            double scaleD = sizeMax * VisData.deformScale / displMax;
            for (int i = 0; i < fem.nNod; i++) {
                for (int j = 0; j < fem.nDim; j++) {
                    fem.setNodeCoord(i, j,
                        fem.getNodeCoord(i, j) +
                        scaleD * VisData.displ[i * fem.nDim + j]);
                }
            }
        }

        setBoundingBox();
        double[] xyzC = new double[3];
        for (int j = 0; j < 3; j++) {
            xyzC[j] = 0.5 * (xyzmin[j] + xyzmax[j]);
        }
        for (int i = 0; i < fem.nNod; i++) {
            for (int j = 0; j < fem.nDim; j++) {
                fem.setNodeCoord(i, j, fem.getNodeCoord(i, j) - xyzC[j]);
            }
        }
    }

    /** Updates the static bounding-box arrays from current node coordinates. */
    void setBoundingBox() {

        for (int j = 0; j < fem.nDim; j++) {
            xyzmin[j] = fem.getNodeCoord(0, j);
            xyzmax[j] = fem.getNodeCoord(0, j);
        }
        for (int i = 1; i < fem.nNod; i++) {
            if (sNodes[i] >= 0) {
                for (int j = 0; j < fem.nDim; j++) {
                    double c = fem.getNodeCoord(i, j);
                    xyzmin[j] = Math.min(xyzmin[j], c);
                    xyzmax[j] = Math.max(xyzmax[j], c);
                }
            }
        }
        if (fem.nDim == 2) {
            xyzmin[2] = -0.01;
            xyzmax[2] =  0.01;
        }
        sizeMax = 0;
        for (int i = 0; i < 3; i++) {
            double s = xyzmax[i] - xyzmin[i];
            sizeMax = Math.max(s, sizeMax);
        }
    }

    /**
     * Returns the uniform scale factor that fits the model into a unit cube.
     *
     * @return scale in (0, 1]
     */
    double getScale() {
        return sizeMax > 0 ? 0.8 / sizeMax : 1.0;
    }
}
