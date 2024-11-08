package spatial.nodes;

import spatial.exceptions.UnimplementedMethodException;
import spatial.kdpoint.KDPoint;
import spatial.knnutils.BoundedPriorityQueue;
import spatial.knnutils.NNData;
import spatial.trees.CentroidAccuracyException;
import spatial.trees.PRQuadTree;

import java.util.ArrayList;
import java.util.Collection;

/** <p>A {@link PRQuadGrayNode} is a gray (&quot;mixed&quot;) {@link PRQuadNode}. It
 * maintains the following invariants: </p>
 * <ul>
 *      <li>Its children pointer buffer is non-null and has a length of 4.</li>
 *      <li>If there is at least one black node child, the total number of {@link KDPoint}s stored
 *      by <b>all</b> of the children is greater than the bucketing parameter (because if it is equal to it
 *      or smaller, we can prune the node.</li>
 * </ul>
 *
 * <p><b>YOU ***** MUST ***** IMPLEMENT THIS CLASS!</b></p>
 *
 *  @author --- Shane Purnell ---
 */
public class PRQuadGrayNode extends PRQuadNode{


    /* ******************************************************************** */
    /* *************  PLACE ANY  PRIVATE FIELDS AND METHODS HERE: ************ */
    /* ********************************************************************** */
	private PRQuadNode[] children;
	private int count;
	
	private boolean noChildren() {
		for (int i = 0; i < 4; i ++) {
			if (children[i] != null) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean oneChild() {
		boolean c = false;
		for (int i = 0; i < 4; i ++) {
			if (children[i] != null) {
				if (c) {
					return false;
				}
				c = true;
			} 
		}
		
		return c;
	}
	
//	private boolean oneChild() {
//	    int countNonNullBlackNodes = 0;
//
//	    for (int i = 0; i < 4; i++) {
//	        if (children[i] != null && children[i] instanceof PRQuadBlackNode) {
//	            countNonNullBlackNodes++;
//
//	            // If there is more than one non-null black node, return false immediately
//	            if (countNonNullBlackNodes > 1 || children[i] instanceof PRQuadGrayNode) {
//	                return false;
//	            }
//	        }
//	    }
//
//	    // Return true only if there is exactly one non-null black node
//	    return countNonNullBlackNodes == 1;
//	}
	
	private PRQuadNode getOnlyChild() {
		for (int i = 0; i < 4; i ++) {
			if (children[i] != null) {
				return children[i];
			}
		}
		
		return null;
	}
	
	private boolean noGrayChildren() {
		for (int i = 0; i < 4; i++) {
			if (children[i] != null && children[i] instanceof PRQuadGrayNode) {
				return false;
			}
		}
		
		return true;
	}
	
	private int sumOfChildren() {
		int sum = 0;
		for (int i = 0; i < 4; i ++) {
			if (children[i] != null) {
				sum += children[i].count();
			}
		}
		
		return sum;
	}
	
	private PRQuadNode collapseChildren() {
		PRQuadBlackNode ret_val = new PRQuadBlackNode(centroid, k, bucketingParam);
		for (int i = 0; i < 4; i ++) {
			if (children[i] != null) {
				KDPoint[] curr = ((PRQuadBlackNode)children[i]).bucket();
				for (int j = 0; j < bucketingParam; j++) {
					if (curr[j] != null) {
						ret_val.insert(curr[j], k);
					}
				}
			}
		}
		
		return ret_val;
	}
	
//	private PRQuadBlackNode collapseChildren() {
//		PRQuadBlackNode ret_val = new PRQuadBlackNode(centroid, k, bucketingParam);
//		for (int i = 0; i < 4; i ++) {
//			if (children[i] != null && children[i] instanceof PRQuadBlackNode) {
//				KDPoint[] curr = ((PRQuadBlackNode)children[i]).bucket();
//				for (int j = 0; j < bucketingParam; j++) {
//					if (curr[j] != null) {
//						ret_val.insert(curr[j], k);
//					}
//				}
//			} else if (children[i] != null && children[i] instanceof PRQuadGrayNode) {
//				children[i] = ((PRQuadGrayNode)children[i]).collapseChildren();
//				i = i - 1;
//			}
//		}
//		
//		return ret_val;
//	}
	
    /* *********************************************************************** */
    /* ***************  IMPLEMENT THE FOLLOWING PUBLIC METHODS:  ************ */
    /* *********************************************************************** */

    /**
     * Creates a {@link PRQuadGrayNode}  with the provided {@link KDPoint} as a centroid;
     * @param centroid A {@link KDPoint} that will act as the centroid of the space spanned by the current
     *                 node.
     * @param k The See {@link PRQuadTree#PRQuadTree(int, int)} for more information on how this parameter works.
     * @param bucketingParam The bucketing parameter fed to this by {@link PRQuadTree}.
     * @see PRQuadTree#PRQuadTree(int, int)
     */
    public PRQuadGrayNode(KDPoint centroid, int k, int bucketingParam){
        super(centroid, k, bucketingParam); // Call to the super class' protected constructor to properly initialize the object!
        
        // Bucketing param will be handled in blacknode
        // Calling convention will be:
        // [0=NW, 1=NE, 2=SW, 3=SE]
        children = new PRQuadNode[4]; 
        count = 0;
    }


    /**
     * <p>Insertion into a {@link PRQuadGrayNode} consists of navigating to the appropriate child
     * and recursively inserting elements into it. If the child is a white node, memory should be allocated for a
     * {@link PRQuadBlackNode} which will contain the provided {@link KDPoint} If it's a {@link PRQuadBlackNode},
     * refer to {@link PRQuadBlackNode#insert(KDPoint, int)} for details on how the insertion is performed. If it's a {@link PRQuadGrayNode},
     * the current method would be called recursively. Polymorphism will allow for the appropriate insert to be called
     * based on the child object's runtime object.</p>
     * @param p A {@link KDPoint} to insert into the subtree rooted at the current {@link PRQuadGrayNode}.
     * @param k The side length of the quadrant spanned by the <b>current</b> {@link PRQuadGrayNode}. It will need to be updated
     *          per recursive call to help guide the input {@link KDPoint}  to the appropriate subtree.
     * @return The subtree rooted at the current node, potentially adjusted after insertion.
     * @see PRQuadBlackNode#insert(KDPoint, int)
     */
    @Override
    public PRQuadNode insert(KDPoint p, int k) {
    	// New points in p
    	int x = p.coords[0], y = p.coords[1];
    	// Points for current centroid
    	int c_x = centroid.coords[0], c_y = centroid.coords[1];
    	int quad;
    	// Points for the new Centroid in each node
    	int newCen_x = (int) ((Math.pow(2, k - 2))), newCen_y = (int) ((Math.pow(2, k - 2)));
    	
    	if (x < c_x && y >= c_y) { // NW Quadrant
    		quad = 0;
			newCen_x = c_x - newCen_x;
    		newCen_y = c_y + newCen_y;
    	} else if (x >= c_x && y >= c_y) { // NE Quadrant
    		quad = 1;
    		newCen_x = c_x + newCen_x;
    		newCen_y = c_y + newCen_y;
    	} else if (x < c_x && y < c_y) { // SW Quadrant
    		quad = 2;
    		newCen_x = c_x - newCen_x;
    		newCen_y = c_y - newCen_y;
    	} else { // SE Quadrant
    		quad = 3;
			newCen_x = c_x + newCen_x;
    		newCen_y = c_y - newCen_y;
    	}
    	
    	// if node is white, create new black node
    	if (children[quad] == null) {
    		children[quad] = new PRQuadBlackNode(new KDPoint(newCen_x, newCen_y), k - 1, bucketingParam, p);
    	} else {
    		children[quad] = children[quad].insert(p, k - 1); 
    	}
    	count ++;
    	return this;
    }

    /**
     * <p>Deleting a {@link KDPoint} from a {@link PRQuadGrayNode} consists of recursing to the appropriate
     * {@link PRQuadBlackNode} child to find the provided {@link KDPoint}. If no such child exists, the search has
     * <b>necessarily failed</b>; <b>no changes should then be made to the subtree rooted at the current node!</b></p>
     *
     * <p>Polymorphism will allow for the recursive call to be made into the appropriate delete method.
     * Importantly, after the recursive deletion call, it needs to be determined if the current {@link PRQuadGrayNode}
     * needs to be collapsed into a {@link PRQuadBlackNode}. This can only happen if it has no gray children, and one of the
     * following two conditions are satisfied:</p>
     *
     * <ol>
     *     <li>The deletion left it with a single black child. Then, there is no reason to further subdivide the quadrant,
     *     and we can replace this with a {@link PRQuadBlackNode} that contains the {@link KDPoint}s that the single
     *     black child contains.</li>
     *     <li>After the deletion, the <b>total</b> number of {@link KDPoint}s contained by <b>all</b> the black children
     *     is <b>equal to or smaller than</b> the bucketing parameter. We can then similarly replace this with a
     *     {@link PRQuadBlackNode} over the {@link KDPoint}s contained by the black children.</li>
     *  </ol>
     *
     * @param p A {@link KDPoint} to delete from the tree rooted at the current node.
     * @return The subtree rooted at the current node, potentially adjusted after deletion.
     */
    @Override
    public PRQuadNode delete(KDPoint p) {
    	double x = p.coords[0], y = p.coords[1];
    	double c_x = centroid.coords[0], c_y = centroid.coords[1];
    	int quad;
    	
    	if (x < c_x && y >= c_y) { // NW Quadrant
    		quad = 0;
    	} else if (x >= c_x && y >= c_y) { // NE Quadrant
    		quad = 1;
    	} else if (x < c_x && y < c_y) { // SW Quadrant
    		quad = 2;
    	} else { // SE Quadrant
    		quad = 3;
    	}
    	
    	children[quad] = children[quad].delete(p);
    	count --;
    	
        if (oneChild()) {
        	return getOnlyChild();
        } else if (sumOfChildren() <= bucketingParam && noGrayChildren()) {
        	return collapseChildren();
        } else if (count == 0) {
        	return null;
        }
        
        
        return this;
    }

    @Override
    public boolean search(KDPoint p){
    	double x = p.coords[0], y = p.coords[1];
    	double c_x = centroid.coords[0], c_y = centroid.coords[1];
    	int quad;
    	
    	if (x < c_x && y >= c_y) { // NW Quadrant
    		quad = 0;
    	} else if (x >= c_x && y >= c_y) { // NE Quadrant
    		quad = 1;
    	} else if (x < c_x && y < c_y) { // SW Quadrant
    		quad = 2;
    	} else { // SE Quadrant
    		quad = 3;
    	}
    	
    	if (children[quad] == null) {
    		return false;
    	}
    	
    	return children[quad].search(p);
    }

    @Override
    public int height(){
    	int currMax = -1;
    	if (noChildren()) {
    		return 0;
    	}
    	
    	if (children[0] != null) {
    		currMax = children[0].height(); 
    	}
    	
    	if (children[1] != null) {
    		currMax = Math.max(currMax, children[1].height());
    	}
    	
    	if (children[2] != null) {
    		currMax = Math.max(currMax, children[2].height());
    	}
    	
    	if (children[3] != null) {
    		currMax = Math.max(currMax, children[3].height());
    	}
    	
        return 1 + currMax;
    }

    @Override
    public int count(){
        return count;
    }

    /**
     * Returns the children of the current node in the form of a Z-ordered 1-D array.
     * @return An array of references to the children of {@code this}. The order is Z (Morton), like so:
     * <ol>
     *     <li>0 is NW</li>
     *     <li>1 is NE</li>
     *     <li>2 is SW</li>
     *     <li>3 is SE</li>
     * </ol>
     */
    public PRQuadNode[] getChildren(){
        return children;
    }

    @Override
    public void range(KDPoint anchor, Collection<KDPoint> results,
                      double range) {
    	double x = anchor.coords[0], y = anchor.coords[1];
    	double c_x = centroid.coords[0], c_y = centroid.coords[1];
    	int quad;
    	
    	if (x < c_x && y >= c_y) { // NW Quadrant
    		quad = 0;
    	} else if (x >= c_x && y >= c_y) { // NE Quadrant
    		quad = 1;
    	} else if (x < c_x && y < c_y) { // SW Quadrant
    		quad = 2;
    	} else { // SE Quadrant
    		quad = 3;
    	}
    	
    	// Greedy towards anchor
    	if (children[quad] != null) {
    		children[quad].range(anchor, results, range);
    	}
    	
    	// Z-Order with pruning
        for (int i = 0; i < 4; i ++) {
        	if (children[i] != null && i != quad && children[i].doesQuadIntersectAnchorRange(anchor, range)) {
        		children[i].range(anchor, results, range);
        	}
        }
    }

    @Override
    public NNData<KDPoint> nearestNeighbor(KDPoint anchor, NNData<KDPoint> n)  {
    	double x = anchor.coords[0], y = anchor.coords[1];
    	double c_x = centroid.coords[0], c_y = centroid.coords[1];
    	int quad;
    	
    	if (x < c_x && y >= c_y) { // NW Quadrant
    		quad = 0;
    	} else if (x >= c_x && y >= c_y) { // NE Quadrant
    		quad = 1;
    	} else if (x < c_x && y < c_y) { // SW Quadrant
    		quad = 2;
    	} else { // SE Quadrant
    		quad = 3;
    	}
    	
    	// Greedy Traversal
    	if (children[quad] != null) {
    		n = children[quad].nearestNeighbor(anchor, n);
    	}
    	
    	// Z-Order with pruning
        for (int i = 0; i < 4; i ++) {
        	if (children[i] != null && i != quad && (children[i].doesQuadIntersectAnchorRange(anchor, n.getBestDist()) || n.getBestDist() == -1.0)) {
        		n = children[i].nearestNeighbor(anchor, n);
        	}
        }
    	
        return n;
    }

    @Override
    public void kNearestNeighbors(int k, KDPoint anchor, BoundedPriorityQueue<KDPoint> queue) {
    	double x = anchor.coords[0], y = anchor.coords[1];
    	double c_x = centroid.coords[0], c_y = centroid.coords[1];
    	int quad;
    	
    	if (x < c_x && y >= c_y) { // NW Quadrant
    		quad = 0;
    	} else if (x >= c_x && y >= c_y) { // NE Quadrant
    		quad = 1;
    	} else if (x < c_x && y < c_y) { // SW Quadrant
    		quad = 2;
    	} else { // SE Quadrant
    		quad = 3;
    	}
    	
    	// Greedy Traversal
    	if (children[quad] != null) {
    		children[quad].kNearestNeighbors(k, anchor, queue);
    	}
    	
    	// Z-Order with pruning
        for (int i = 0; i < 4; i ++) {
        	if (children[i] != null && i != quad && (queue.size() < k || children[i].doesQuadIntersectAnchorRange(anchor, queue.last().euclideanDistance(anchor)))) {
        		children[i].kNearestNeighbors(k, anchor, queue);
        	}
        }
    	
        return;
    }
}




















