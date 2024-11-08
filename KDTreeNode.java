package spatial.nodes;

import spatial.exceptions.UnimplementedMethodException;
import spatial.kdpoint.KDPoint;
import spatial.knnutils.BoundedPriorityQueue;
import spatial.knnutils.NNData;

import java.util.Collection;

/**
 * <p>{@link KDTreeNode} is an abstraction over nodes of a KD-Tree. It is used extensively by
 * {@link spatial.trees.KDTree} to implement its functionality.</p>
 *
 * <p><b>YOU ***** MUST ***** IMPLEMENT THIS CLASS!</b></p>
 *
 * @author  ---- Shane Purnell -----
 *
 * @see spatial.trees.KDTree
 */
public class KDTreeNode {


    /* *************************************************************************** */
    /* ************* WE PROVIDE THESE FIELDS TO GET YOU STARTED.  **************** */
    /* ************************************************************************** */
    private KDPoint p;
    private int height;
    private KDTreeNode left, right;

    /* *************************************************************************************** */
    /* *************  PLACE ANY OTHER PRIVATE FIELDS AND YOUR PRIVATE METHODS HERE: ************ */
    /* ************************************************************************************* */
    
    private KDTreeNode findMin(KDTreeNode currNode, int soughtDim, int currDim, int dims) {
    	if (currNode == null) {
    		return null;
    	}
    	
    	if (currNode.left == null && currNode.right == null) {
    		return currNode;
    	}
    	
    	if (soughtDim % dims == currDim % dims) {
    		if (currNode.left == null) {
    			return currNode;
    		} else {
    			return findMin(currNode.left, soughtDim, currDim + 1, dims);
    		}
    	}
    	
    	KDTreeNode lmin = findMin(currNode.left, soughtDim, currDim + 1, dims);
    	KDTreeNode rmin = findMin(currNode.right, soughtDim, currDim + 1, dims);
    	return min3(lmin, rmin, currNode, soughtDim, dims);
    }
    
    private KDTreeNode min3(KDTreeNode lmin, KDTreeNode rmin, KDTreeNode curr, int soughtDim, int dims) {
    	int l, r, c; 
    	if (lmin == null) {
    		l = 999999999;
    	} else {l = lmin.p.coords[soughtDim % dims];}
    	if (rmin == null) {
    		r = 999999999;
    	} else {r = rmin.p.coords[soughtDim % dims];}
    	if (curr == null) {
    		c = 999999999;
    	} else {c = curr.p.coords[soughtDim % dims];}
    	
    	int res = Math.min(l, (Math.min(r, c)));
    	
    	if (res == l) { return lmin;}
    	else if (res == r) {return rmin;}
    	else {return curr;}
    }
    
    /* *********************************************************************** */
    /* ***************  IMPLEMENT THE FOLLOWING PUBLIC METHODS:  ************ */
    /* *********************************************************************** */


    /**
     * 1-arg constructor. Stores the provided {@link KDPoint} inside the freshly created node.
     * @param p The {@link KDPoint} to store inside this. Just a reminder: {@link KDPoint}s are
     *          <b>mutable!!!</b>.
     */
    public KDTreeNode(KDPoint p){
        this.p = p;
        left = right = null;
        height = 0;
        
    }
    
    private KDTreeNode(KDPoint p, int h) {
    	 this.p = p;
         left = right = null;
         height = h;
    }

    /**
     * <p>Inserts the provided {@link KDPoint} in the tree rooted at this. To select which subtree to recurse to,
     * the KD-Tree acts as a Binary Search Tree on currDim; it will examine the value of the provided {@link KDPoint}
     * at currDim and determine whether it is larger than or equal to the contained {@link KDPoint}'s relevant dimension
     * value. If so, we recurse right, like a regular BST, otherwise left.</p>
     * @param currDim The current dimension to consider
     * @param dims The total number of dimensions that the space considers.
     * @param pIn The {@link KDPoint} to insert into the node.
     * @see #delete(KDPoint, int, int)
     */
    // Insert into tree if null
	// else we continue down the tree
    public  void insert(KDPoint pIn, int currDim, int dims){
    	int dimmy = currDim % (dims); // Modulus to properly index dimensions
    	if (pIn.coords[dimmy] < p.coords[dimmy]) { // Go left
        	if (left == null) {
        		left = new KDTreeNode(pIn, currDim + 1);
        	} else {
        		left.insert(pIn, currDim + 1, dims);
        	}
        } else { // Go right
        	if (right == null) {
        		right = new KDTreeNode(pIn, currDim + 1);
        	} else {
        		right.insert(pIn, currDim + 1, dims);
        	}
        }
    }

    /**
     * <p>Deletes the provided {@link KDPoint} from the tree rooted at this. To select which subtree to recurse to,
     * the KD-Tree acts as a Binary Search Tree on currDim; it will examine the value of the provided {@link KDPoint}
     * at currDim and determine whether it is larger than or equal to the contained {@link KDPoint}'s relevant dimension
     * value. If so, we recurse right, like a regular BST, otherwise left. There exist two special cases of deletion,
     * depending on whether we are deleting a {@link KDPoint} from a node who either:</p>
     *
     * <ul>
     *      <li>Has a NON-null subtree as a right child.</li>
     *      <li>Has a NULL subtree as a right child.</li>
     * </ul>
     *
     * <p>You should consult the class slides, your notes, and the textbook about what you need to do in those two
     * special cases.</p>
     * @param currDim The current dimension to consider.
     * @param dims The total number of dimensions that the space considers.
     * @param pIn The {@link KDPoint} to delete from the node.
     * @see #insert(KDPoint, int, int)
     * @return A reference to this after the deletion takes place.
     */
    public KDTreeNode delete(KDPoint pIn, int currDim, int dims){
    	int dimmy = currDim % (dims);
    	if (p.equals(pIn)) { // Node found case
    		if (left == null && right == null) {
        		return null;
        		
        	} else if (right != null) {
        		// replace curr with inorder successor in currDim and delete recursively
        		KDTreeNode min = findMin(this.right, dimmy, this.right.height % dims, dims);
        		this.p = new KDPoint(min.p);
        		this.right = this.right.delete(min.p, currDim + 1, dims);
        		
        	} else if (left != null) { // Non-null left tree
        		// replace curr with min value in currDim, move left subtree to the right and delete min recursively
        		KDTreeNode min = findMin(this.left, dimmy, this.left.height % dims, dims);
        		this.p = new KDPoint(min.p);
        		this.right = this.left;
        		this.left = null;
        		this.right = this.right.delete(min.p, currDim + 1, dims);
        		
        	}
    		
    	} else if (pIn.coords[dimmy] < p.coords[dimmy] && left != null) { // traversal cases
    		this.left = left.delete(pIn, currDim + 1, dims);
    	} else if (right != null){
    		this.right = right.delete(pIn, currDim + 1, dims);
    	}
    	
    	// Update the height of the current node
    	return this;
    }

    /**
     * Searches the subtree rooted at the current node for the provided {@link KDPoint}.
     * @param pIn The {@link KDPoint} to search for.
     * @param currDim The current dimension considered.
     * @param dims The total number of dimensions considered.
     * @return true iff pIn was found in the subtree rooted at this, false otherwise.
     */
    public boolean search(KDPoint pIn, int currDim, int dims){
    	int dimmy = currDim % (dims);
    	
    	if (this.p.equals(pIn)) {
        	return true;
        } else if (this.right == null && this.left == null) {
        	return false;
        } else if (pIn.coords[dimmy] < this.p.coords[dimmy]) {
        	if (left == null) {
        		return false;
        	}
        	return left.search(pIn, currDim + 1, dims);
        } else {
        	if (right == null) {
        		return false;
        	}
        	return right.search(pIn, currDim + 1, dims);
        }
    }

    /**
     * <p>Executes a range query in the given {@link KDTreeNode}. Given an &quot;anchor&quot; {@link KDPoint},
     * all {@link KDPoint}s that have a {@link KDPoint#euclideanDistance(KDPoint) euclideanDistance} of <b>at most</b> range
     * <b>INCLUSIVE</b> from the anchor point <b>except</b> for the anchor itself should be inserted into the {@link Collection}
     * that is passed.</p>
     *
     * <p>Remember: range queries behave <em>greedily</em> as we go down (approaching the anchor as &quot;fast&quot;
     * as our currDim allows and <em>prune subtrees</em> that we <b>don't</b> have to visit as we backtrack. Consult
     * all of our resources if you need a reminder of how these should work.</p>
     *
     * @param anchor The centroid of the hypersphere that the range query implicitly creates.
     * @param results A {@link Collection} that accumulates all the {@link }
     * @param currDim The current dimension examined by the {@link KDTreeNode}.
     * @param dims The total number of dimensions of our {@link KDPoint}s.
     * @param range The <b>INCLUSIVE</b> range from the &quot;anchor&quot; {@link KDPoint}, within which all the
     *              {@link KDPoint}s that satisfy our query will fall. The euclideanDistance metric used} is defined by
     *              {@link KDPoint#euclideanDistance(KDPoint)}.
     */
    public void range(KDPoint anchor, Collection<KDPoint> results,
                      double range, int currDim , int dims){
    	boolean lFlag;
    	// if current node is in range, add to results
    	if (this.p.euclideanDistance(anchor) <= range && !this.p.equals(anchor)) {
    		results.add(this.p);
    	}
    	
    	// Greedy
    	int dimmy = currDim % dims;
    	if (anchor.coords[dimmy] < this.p.coords[dimmy]) {
    		if (this.left != null) {
    			this.left.range(anchor, results, range, currDim + 1, dims);
    		}
    		lFlag = true;
    	} else {
    		if (this.right != null) {
    			this.right.range(anchor, results, range, currDim + 1, dims);
    		}
    		lFlag = false;
    	}
    	
    	// Pruning
    	if (lFlag) { // If we went to left before, we must now check right
    		if (this.right != null) {
	    		if (Math.abs(anchor.coords[dimmy] - this.p.coords[dimmy]) <= range) {
	    			this.right.range(anchor, results, range, currDim + 1, dims);
	    		} else {
	    			return;
	    		}
    		}
    	} else { // Else, we must check left this time
    		if (this.left != null) {
	    		if (Math.abs(anchor.coords[dimmy] - this.p.coords[dimmy]) <= range) {
	    			this.left.range(anchor, results, range, currDim + 1, dims);
	    		} else {
	    			return;
	    		}
    		}
    	}
    
    }


    /**
     * <p>Executes a nearest neighbor query, which returns the nearest neighbor, in terms of
     * {@link KDPoint#euclideanDistance(KDPoint)}, from the &quot;anchor&quot; point.</p>
     *
     * <p>Recall that, in the descending phase, a NN query behaves <em>greedily</em>, approaching our
     * &quot;anchor&quot; point as fast as currDim allows. While doing so, it implicitly
     * <b>bounds</b> the acceptable solutions under the current <b>best solution</b>, which is passed as
     * an argument. This approach is known in Computer Science as &quot;branch-and-bound&quot; and it helps us solve an
     * otherwise exponential complexity problem (nearest neighbors) efficiently. Remember that when we want to determine
     * if we need to recurse to a different subtree, it is <b>necessary</b> to compare the euclideanDistance reported by
     * {@link KDPoint#euclideanDistance(KDPoint)} and coordinate differences! Those are comparable with each other because they
     * are the same data type ({@link Double}).</p>
     *
     * @return An object of type {@link NNData}, which exposes the pair (distance_of_NN_from_anchor, NN),
     * where NN is the nearest {@link KDPoint} to the anchor {@link KDPoint} that we found.
     *
     * @param anchor The &quot;ancor&quot; {@link KDPoint}of the nearest neighbor query.
     * @param currDim The current dimension considered.
     * @param dims The total number of dimensions considered.
     * @param n An object of type {@link NNData}, which will define a nearest neighbor as a pair (distance_of_NN_from_anchor, NN),
     *      * where NN is the nearest neighbor found.
     *
     * @see NNData
     * @see #kNearestNeighbors(int, KDPoint, BoundedPriorityQueue, int, int)
     */
    public  NNData<KDPoint> nearestNeighbor(KDPoint anchor, int currDim,
                                            NNData<KDPoint> n, int dims){
    	boolean lFlag;
    	int dimmy = currDim % dims;
    	double anchorDist = anchor.euclideanDistance(this.p);
    	if ((n.getBestDist() == -1.0 || anchorDist < n.getBestDist()) && !this.p.equals(anchor)) { // Update if our currNode is closer
    		n.update(this.p, anchorDist);
    	}
    	
    	// Greedy Traversal
    	if (anchor.coords[dimmy] < this.p.coords[dimmy]) { // Traverse left
    		if (this.left != null) {
    			this.left.nearestNeighbor(anchor, currDim + 1, n, dims);
    		}
    		lFlag = true;
    	} else { // Traverse right
    		if (this.right != null) {
    			this.right.nearestNeighbor(anchor, currDim + 1, n, dims);
    		}
    		lFlag = false;
    	}
    	
    	// Pruning w/ conditionals
    	if (lFlag) {
    		if (this.right != null) {
    			if ((anchor.coords[dimmy] - this.right.p.coords[dimmy]) <= n.getBestDist()) {
    				this.right.nearestNeighbor(anchor, currDim + 1, n, dims);
    			}
    		}
    	} else {
    		if (this.left != null) {
    			if ((anchor.coords[dimmy] - this.left.p.coords[dimmy]) <= n.getBestDist()) {
    				this.left.nearestNeighbor(anchor, currDim + 1, n, dims);
    			}
    		}
    	}
    	
    	return n;
    }

    /**
     * <p>Executes a nearest neighbor query, which returns the nearest neighbor, in terms of
     * {@link KDPoint#euclideanDistance(KDPoint)}, from the &quot;anchor&quot; point.</p>
     *
     * <p>Recall that, in the descending phase, a NN query behaves <em>greedily</em>, approaching our
     * &quot;anchor&quot; point as fast as currDim allows. While doing so, it implicitly
     * <b>bounds</b> the acceptable solutions under the current <b>worst solution</b>, which is maintained as the
     * last element of the provided {@link BoundedPriorityQueue}. This is another instance of &quot;branch-and-bound&quot;
     * Remember that when we want to determine if we need to recurse to a different subtree, it is <b>necessary</b>
     * to compare the euclideanDistance reported by* {@link KDPoint#euclideanDistance(KDPoint)} and coordinate differences!
     * Those are comparable with each other because they are the same data type ({@link Double}).</p>
     *
     * <p>The main difference of the implementation of this method and the implementation of
     * {@link #nearestNeighbor(KDPoint, int, NNData, int)} is the necessity of using the class
     * {@link BoundedPriorityQueue} effectively. Consult your various resources
     * to understand how you should be using this class.</p>
     *
     * @param k The total number of neighbors to retrieve. It is better if this quantity is an odd number, to
     *          avoid ties in Binary Classification tasks.
     * @param anchor The &quot;anchor&quot; {@link KDPoint} of the nearest neighbor query.
     * @param currDim The current dimension considered.
     * @param dims The total number of dimensions considered.
     * @param queue A {@link BoundedPriorityQueue} that will maintain at most k nearest neighbors of
     *              the anchor point at all times, sorted by euclideanDistance to the point.
     *
     * @see BoundedPriorityQueue
     */
    public  void kNearestNeighbors(int k, KDPoint anchor, BoundedPriorityQueue<KDPoint> queue, int currDim, int dims){
    	boolean lFlag;
    	int dimmy = currDim % dims;
    	double anchorDist = anchor.euclideanDistance(this.p);
    	if (!this.p.equals(anchor)) {
    		queue.enqueue(this.p, anchorDist);
    	}
    	
    	// Greedy Traversal
    	if (anchor.coords[dimmy] < this.p.coords[dimmy]) { // Traverse left
    		if (this.left != null) {
    			this.left.kNearestNeighbors(k, anchor, queue, currDim + 1, dims);
    		}
    		lFlag = true;
    	} else { // Traverse right
    		if (this.right != null) {
    			this.right.kNearestNeighbors(k, anchor, queue, currDim + 1, dims);
    		}
    		lFlag = false;
    	}
    	
    	// Pruning w/ conditionals
    	if (lFlag) {
    		if (this.right != null) {
    			if ((anchor.coords[dimmy] - this.p.coords[dimmy]) <= queue.last().euclideanDistance(anchor)) {
    				this.right.kNearestNeighbors(k, anchor, queue, currDim + 1, dims);
    			}
    		}
    	} else {
    		if (this.left != null) {
    			if ((anchor.coords[dimmy] - this.p.coords[dimmy]) <= queue.last().euclideanDistance(anchor)) {
    				this.left.kNearestNeighbors(k, anchor, queue, currDim + 1, dims);
    			}
    		}
    	}
    	
    	return;
    }

    /**
     * Returns the height of the subtree rooted at the current node. Recall our definition of height for binary trees:
     * <ol>
     *     <li>A null tree has a height of -1.</li>
     *     <li>A non-null tree has a height equal to max(height(left_subtree), height(right_subtree))+1</li>
     * </ol>
     * @return the height of the subtree rooted at the current node.
     */
    public int height() {
    	if (this.p == null) {
    		return -1;
    	}
        if (left == null && right == null) {
        	return this.height;
        } else if (left != null && right == null) {
        	return left.height();
        } else if (right != null && left == null) {
        	return right.height();
        } else { 
        	return Math.max(left.height(), right.height());
        }
    }

    /**
     * A simple getter for the {@link KDPoint} held by the current node. Remember: {@link KDPoint}s ARE
     * MUTABLE, SO WE NEED TO DO DEEP COPIES!!!
     * @return The {@link KDPoint} held inside this.
     */
    public KDPoint getPoint(){
        return new KDPoint(this.p);
    }

    public KDTreeNode getLeft(){
    	return this.left;
    }

    public KDTreeNode getRight(){
        return this.right;
    }
}
