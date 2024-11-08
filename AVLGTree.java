package avlg;

import avlg.exceptions.UnimplementedMethodException;
import avlg.exceptions.EmptyTreeException;
import avlg.exceptions.InvalidBalanceException;

/** <p>{@link AVLGTree}  is a class representing an <a href="https://en.wikipedia.org/wiki/AVL_tree">AVL Tree</a> with
 * a relaxed balance condition. Its constructor receives a strictly  positive parameter which controls the <b>maximum</b>
 * imbalance allowed on any subtree of the tree which it creates. So, for example:</p>
 *  <ul>
 *      <li>An AVL-1 tree is a classic AVL tree, which only allows for perfectly balanced binary
 *      subtrees (imbalance of 0 everywhere), or subtrees with a maximum imbalance of 1 (somewhere). </li>
 *      <li>An AVL-2 tree relaxes the criteria of AVL-1 trees, by also allowing for subtrees
 *      that have an imbalance of 2.</li>
 *      <li>AVL-3 trees allow an imbalance of 3.</li>
 *      <li>...</li>
 *  </ul>
 *
 *  <p>The idea behind AVL-G trees is that rotations cost time, so maybe we would be willing to
 *  accept bad search performance now and then if it would mean less rotations. On the other hand, increasing
 *  the balance parameter also means that we will be making <b>insertions</b> faster.</p>
 *
 * @author Shane Purnell
 *
 * @see EmptyTreeException
 * @see InvalidBalanceException
 * @see StudentTests
 */
public class AVLGTree<T extends Comparable<T>> {

    /* ********************************************************* *
     * Write any private data elements or private methods here...*
     * ********************************************************* */
	private int g;
	private int total_nodes;
	private Node root;
	
	private class Node {
		private T value;
		private Node left;
		private Node right;
		private int height;
		
		private Node(T key) {
			this.value = key;
			this.left = null;
			this.right = null;
			this.height = 1;
		}
	}
	
	private Node rotateRight(Node curr) {
		Node temp = curr.left;
		curr.left = temp.right;
		temp.right = curr;
		
		curr.height = getHeight(curr);
		temp.height = getHeight(temp);
		
		if (curr == root) {
			root = temp;
		}
		
		return temp;
	}
	
	private Node rotateLeft(Node curr) {
		Node temp = curr.right;
		curr.right = temp.left;
		temp.left = curr;
		
		curr.height = getHeight(curr);
		temp.height = getHeight(temp);
		
		if (curr == root) {
			root = temp;
		}
		
		return temp;
	}
	
	private Node rotateLR(Node curr) {
		curr.left = rotateLeft(curr.left);
		curr = rotateRight(curr);
		return curr;
	}
	
	private Node rotateRL(Node curr) {
		curr.right = rotateRight(curr.right);
		curr = rotateLeft(curr);
		return curr;
	}
	
	private Node getMin(Node curr) {
		if (curr == null) {
			return null;
		}
		if (curr.left == null) {
			return curr;
		} else {
			return getMin(curr.left);
		}
	}
	
	private Node inOrderSuccessor(Node r, Node curr) {
		Node successor = null;
        while (root != null) {
            if (curr.value.compareTo(r.value) < 0) {
                successor = r;
                r = r.left;
            } else if (curr.value.compareTo(r.value) > 0) {
                r = r.right;
            } else {
                if (r.right != null) {
                    successor = getMin(r.right);
                }
                break;
            }
        }
        return successor;
	}
	
    /* ******************************************************** *
     * ************************ PUBLIC METHODS **************** *
     * ******************************************************** */

    /**
     * The class constructor provides the tree with the maximum imbalance allowed.
     * @param maxImbalance The maximum imbalance allowed by the AVL-G Tree.
     * @throws InvalidBalanceException if maxImbalance is a value smaller than 1.
     */
    public AVLGTree(int maxImbalance) throws InvalidBalanceException {
    	if (maxImbalance < 1) {
    		throw new InvalidBalanceException("Max Imbalance must be >= 1.");
    	}
        this.g = maxImbalance;
        this.total_nodes = 0;
        this.root = null;
    }

    /**
     * Insert key in the tree. You will <b>not</b> be tested on
     * duplicates! This means that in a deletion test, any key that has been
     * inserted and subsequently deleted should <b>not</b> be found in the tree!
     * s
     * @param key The key to insert in the tree.
     */
    public void insert(T key) { 
    	if (root == null) {
    		root = new Node(key);
    		total_nodes ++;
    		return;
    	}
    	
        root = insert_aux(this.root, key);
        total_nodes += 1;
        
        return;  
    }
    
    private Node insert_aux(Node curr, T key) {
    	if (curr == null) {
    		return new Node(key);
    	}
    	
    	if (key.compareTo(curr.value) < 0) { // go left
    		curr.left = insert_aux(curr.left, key);
    		if ((curr.right == null && curr.left.height > g)) {
    			if (key.compareTo(curr.left.value) < 0) {
    				curr = rotateRight(curr);
    			} else {
    				curr = rotateLR(curr);
    			}
    		}
    		if (curr.right != null && curr.left.height - curr.right.height > g) { 
    			if (key.compareTo(curr.left.value) < 0) {
    				curr = rotateRight(curr);
    			} else {
    				curr = rotateLR(curr);
    			}
    		}
    	} else { // go right
    		curr.right = insert_aux(curr.right, key);
    		if (curr.left == null && curr.right.height > g) {
    			if (key.compareTo(curr.right.value) < 0) {
					curr = rotateRL(curr);
				} else {
					curr = rotateLeft(curr);
				}
    		}
			if (curr.left != null && curr.right.height - curr.left.height > g) {
				if (key.compareTo(curr.right.value) < 0) {
					curr = rotateRL(curr);
				} else {
					curr = rotateLeft(curr);
				}
			}			
		}
    	
    	if (curr.left == null) {
    		curr.height = curr.right.height + 1;
    	} else if (curr.right == null) {
    		curr.height = curr.left.height + 1;
    	} else {
    		curr.height = Math.max(curr.left.height, curr.right.height) + 1; 
    	}
    	return curr;
    }

    /**
     * Delete the key from the data structure and return it to the caller.
     * @param key The key to delete from the structure.
     * @return The key that was removed, or {@code null} if the key was not found.
     * @throws EmptyTreeException if the tree is empty.
     */
    public T delete(T key) throws EmptyTreeException {
    	if (root == null) {
   		 throw new EmptyTreeException("Tree is empty");
	   	 } 
	   	 
	   	 if (root.left == null && root.value.compareTo(key) == 0 && root.right == null) {
	   		 T temp = root.value;
	   		 root = null;
	   		 total_nodes --;
	   		 return temp;
	   	 }
	   	 
	   	 if (root.right == null && root.value.compareTo(key) == 0) {
	   		 T temp = root.value;
	   		 root = root.left;
	   		 total_nodes --;
	   		 root.height = 0;
	   		 return temp;
	   	 } 
	   	 
	   	 if (root.left == null && root.value.compareTo(key) == 0) {
	   		 T temp = root.value;
	   		 root = root.right;
	   		 total_nodes --;
	   		 root.height = 0;
	   		 return temp;
	   	 }
	   	 
	   	 Node res = delete_aux(root, key);
	   	 if (res == null) {
	   		 return null;
	   	 } else {
	   		 total_nodes --;
	   		 return key;
	   	 }
    }
    
    private Node delete_aux(Node curr, T key) {
    	if (curr == null) {
    		return null;
    	}
    	
    	if (key.compareTo(curr.value) < 0) {
    		curr.left = delete_aux(curr.left, key);
    	} else if (key.compareTo(curr.value) > 0) {
    		curr.right = delete_aux(curr.right, key);
    	} else {
    		if ((curr.left == null) || (curr.right == null)) {
    			Node temp = null;
    			if (curr.left == null) {
    				temp = curr.right;
    			} else {
    				temp = curr.left;
    			}
    			
    			if (temp == null) {
    				temp = curr;
    				curr = null;
    			} else {
    				curr = temp;
    			}
    		} else {
    			Node temp = getMin(curr.right);
    			curr.value = temp.value;
    			curr.right = delete_aux(curr.right, temp.value);
    		}
    	}
    	
    	if (curr == null || (curr.left == null && curr.right == null)) {
    		return curr;
    	}
    	
    	curr.height = 1 + Math.max(Math.abs(getHeight(curr.left)), Math.abs(getHeight(curr.right)));
    	int bal = getHeight(curr.left) - getHeight(curr.right);
    	
    	if (bal > g && (getHeight(curr.left.left) - getHeight(curr.left.right)) >= 0) {
    		return rotateRight(curr);
    	}
    	if (bal > g && (getHeight(curr.left.left) - getHeight(curr.left.right)) < 0) {
    		curr.left = rotateLeft(curr.left);
    		return rotateRight(curr);
    	}
    	if (Math.abs(bal) > g && (getHeight(curr.right.left) - getHeight(curr.right.right)) <= 0) {
    		return rotateLeft(curr);
    	}
    	if (Math.abs(bal) > g && (getHeight(curr.right.left) - getHeight(curr.right.right)) > 0) {
    		curr.right = rotateRight(curr.right);
    		return rotateLeft(curr);
    	}
    	
    	return curr;
    }
    
    /**
     * <p>Search for key in the tree. Return a reference to it if it's in there,
     * or {@code null} otherwise.</p>
     * @param key The key to search for.
     * @return key if key is in the tree, or {@code null} otherwise.
     * @throws EmptyTreeException if the tree is empty.
     */
    public T search(T key) throws EmptyTreeException {
        if (root == null) {
        	throw new EmptyTreeException("Tree is empty");
        } 
        if (root.value.compareTo(key) == 0) {
        	return root.value;
        } else {
        	return search_aux(key, root);
        }
    }
    
    private T search_aux(T key, Node curr) {
    	// Base cases
    	if (curr == null) {
    		return null;
    	} else if (curr.value.compareTo(key) == 0) {
    		return curr.value;
    	}
    	
    	// Decide left or right
    	if (key.compareTo(curr.value) < 0) {
    		if (curr.left == null) {
    			return null;
    		}
    		return search_aux(key, curr.left);
    	} else {
    		if (curr.right == null) {
    			return null;
    		}
    		return search_aux(key, curr.right);
    	}
    }
    
    private Node search_node(T key, Node curr) {
    	if (curr == null) {
    		return null;
    	} else if (curr.value.compareTo(key) == 0) {
    		return curr;
    	}
    	
    	if (key.compareTo(curr.value) < 0) {
    		if (curr.left == null) {
    			return null;
    		}
    		return search_node(key, curr.left);
    	} else {
    		if (curr.right == null) {
    			return null;
    		}
    		return search_node(key, curr.right);
    	}
    	
    }

    /**
     * Retrieves the maximum imbalance parameter.
     * @return The maximum imbalance parameter provided as a constructor parameter.
     */
    public int getMaxImbalance(){
        return g;
    }

    /**
     * <p>Return the height of the tree. The height of the tree is defined as the length of the
     * longest path between the root and the leaf level. By definition of path length, a
     * stub tree has a height of 0, and we define an empty tree to have a height of -1.</p>
     * @return The height of the tree. If the tree is empty, returns -1.
     */
    public int getHeight() {
    	return getHeight_aux(root) - 1;
    }
    
    private int getHeight(Node curr) {
    	return getHeight_aux(curr);
    }
    
    private int getHeight_aux(Node curr) {
    	if (curr == null) {
    		return 0;
    	}
    	int l = getHeight_aux(curr.left);
    	int r = getHeight_aux(curr.right);
    	return 1 + Math.max(l, r);
    }

    /**
     * Query the tree for emptiness. A tree is empty iff it has zero keys stored.
     * @return {@code true} if the tree is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
        if (root == null) {
        	return true;
        } else {
        	return false;
        }
    }

    /**
     * Return the key at the tree's root node.
     * @return The key at the tree's root node.
     * @throws  EmptyTreeException if the tree is empty.
     */
    public T getRoot() throws EmptyTreeException{
    	if (root == null) {
    		throw new EmptyTreeException("tree is empty");
    	}
        return root.value;
    }


    /**
     * <p>Establishes whether the AVL-G tree <em>globally</em> satisfies the BST condition. This method is
     * <b>terrifically useful for testing!</b></p>
     * @return {@code true} if the tree satisfies the Binary Search Tree property,
     * {@code false} otherwise.
     */
    public boolean isBST() {
        return isBST_aux(root);
    }
    
    private boolean isBST_aux(Node curr) {
    	if (curr == null || curr.left == null) {
    		return true;
    	}
    	
    	if (curr.value.compareTo(curr.left.value) < 0) {
    		return false;
    	} else if (curr.right == null) { 
    		return true;
    	} if (curr.value.compareTo(curr.right.value) > 0) {
    		return false;
    	} else {
    		return isBST_aux(curr.left) && isBST_aux(curr.right); 
    	}
    }


    /**
     * <p>Establishes whether the AVL-G tree <em>globally</em> satisfies the AVL-G condition. This method is
     * <b>terrifically useful for testing!</b></p>
     * @return {@code true} if the tree satisfies the balance requirements of an AVLG tree, {@code false}
     * otherwise.
     */
    public boolean isAVLGBalanced() {
    	if (root == null) {
    		return true;
    	}
        if (Math.abs(getHeight(root.left) - getHeight(root.right)) <= g) {
        	return true;
        } else {
        	return false;
        }
    }

    /**
     * <p>Empties the AVL-G Tree of all its elements. After a call to this method, the
     * tree should have <b>0</b> elements.</p>
     */
    public void clear() {
        root = null;
        total_nodes = 0;
    }


    /**
     * <p>Return the number of elements in the tree.</p>
     * @return  The number of elements in the tree.
     */
    public int getCount(){
        return total_nodes;
    }
}
