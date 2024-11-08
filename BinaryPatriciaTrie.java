package bpt;

import bpt.UnimplementedMethodException;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>{@code BinaryPatriciaTrie} is a Patricia Trie over the binary alphabet &#123;	 0, 1 &#125;. By restricting themselves
 * to this small but terrifically useful alphabet, Binary Patricia Tries combine all the positive
 * aspects of Patricia Tries while shedding the storage cost typically associated with tries that
 * deal with huge alphabets.</p>
 *
 * @author YOUR NAME HERE!
 */
public class BinaryPatriciaTrie {

    /* We are giving you this class as an example of what your inner node might look like.
     * If you would prefer to use a size-2 array or hold other things in your nodes, please feel free
     * to do so. We can *guarantee* that a *correct* implementation exists with *exactly* this data
     * stored in the nodes.
     */
    private static class TrieNode {
        private TrieNode left, right, parent;
        private String str;
        private boolean isKey;

        // Default constructor for your inner nodes.
        TrieNode() {
            this("", false);
        }

        // Non-default constructor.
        TrieNode(String str, boolean isKey) {
            left = right = parent = null;
            this.str = str;
            this.isKey = isKey;
        }
    }

    private TrieNode root;
    private int count;

    /**
     * Simple constructor that will initialize the internals of {@code this}.
     */
    public BinaryPatriciaTrie() {
        root = new TrieNode();
        count = 0;
    }

    /**
     * Searches the trie for a given key.
     *
     * @param key The input {@link String} key.
     * @return {@code true} if and only if key is in the trie, {@code false} otherwise.
     */
    public boolean search(String key) {
    	if (count == 0) {
    		return false;
    	}
    	String curr_str = key;
    	TrieNode curr_node = root;
    	
    	while (curr_node != null && !curr_str.equals("")) {
    		if (curr_str.equals(curr_node.str) && curr_node.isKey) {
    			return true;
    		} else if (curr_str.equals(curr_node.str) && !curr_node.isKey) {
    			return false;
    		}
    		
	    	int len = common_prefix(curr_str, curr_node.str);
	    	if (len < curr_node.str.length()) {
	    		break;
	    	}
	    	curr_str = curr_str.substring(len);    		
	    	
    		if (curr_str.charAt(0) == '0') {
    			curr_node = curr_node.left;
    		} else {
    			curr_node = curr_node.right;
    		}
    		
    	}
    	
    	return false;
    	
    }
    
    private int common_prefix(String prev, String key) {
    	int min = Math.min(prev.length(), key.length()), count = 0;
    	while (count < min && (prev.charAt(count) == key.charAt(count))) {
    		count ++;
    	}
    	
    	return count;
    }

    /**
     * Inserts key into the trie.
     *
     * @param key The input {@link String}  key.
     * @return {@code true} if and only if the key was not already in the trie, {@code false} otherwise.
     */
    
    public boolean insert(String key) {
    	String curr_str = key;
    	TrieNode curr_node = root, prev = root;
    	int len;
    	
    	while (curr_node != null && !curr_str.equals("")) {
    		if (curr_str.equals(curr_node.str) && curr_node.isKey) {
    			return false;
    		} else if (curr_str.equals(curr_node.str) && !curr_node.isKey) {
    			count ++;
    			curr_node.isKey = true;
    			return true;
    		}
    		
	    	len = common_prefix(curr_str, curr_node.str);
	    	if (len < curr_node.str.length()) {
	    		break;
	    	}
	    	curr_str = curr_str.substring(len); 
	    	prev = curr_node;
	    	//prev_str = curr_node.str;
    		if (curr_str.charAt(0) == '0') {
    			curr_node = curr_node.left;
    		} else {
    			curr_node = curr_node.right;
    		}
    		
    	}
    	
    	// We hit a null case (allocate new child node)
    	if (curr_node == null) {
    		if (curr_str.charAt(0) == '0') {
    			prev.left = new TrieNode(curr_str, true);
    			prev.left.parent = prev;
    		} else {
    			prev.right = new TrieNode(curr_str, true);
    			prev.right.parent = prev;
    		}
    	} else if (curr_node.str.startsWith(curr_str)) { // New Key == prefix (create new parent)
    		TrieNode new_parent = new TrieNode(curr_str, true);
    		new_parent.parent = prev;
    		len = common_prefix(curr_node.str, curr_str);
    		TrieNode temp = curr_node;
    		temp.str = temp.str.substring(len);
    		curr_node = new_parent;
    		if (temp.str.charAt(0) == '0') {
    			curr_node.left = temp;
    		} else {
    			curr_node.right = temp;
    		}
    		
    		if (new_parent.str.charAt(0) == '0') {
    			prev.left = curr_node;
    		} else {
    			prev.right = curr_node;
    		}
    	} else if (common_prefix(curr_node.str, curr_str) != 0) { // New key shares a prefix (split and create two new children)
    		len = common_prefix(curr_node.str, curr_str);
    		TrieNode new_parent = new TrieNode(curr_str.substring(0, len), false);
    		new_parent.parent = prev;
    		TrieNode child_1 = new TrieNode(curr_node.str.substring(len), curr_node.isKey);
    		child_1.parent = new_parent;
    		TrieNode child_2 = new TrieNode(curr_str.substring(len), true);
    		child_2.parent = new_parent;
    		
    		// Bring over old parent's children:
    		child_1.left = curr_node.left;
    		child_1.right = curr_node.right;
    		
    		// assign child 1 and 2 to left or right
    		if (child_1.str.compareTo(child_2.str) < 0) {
    			new_parent.left = child_1;
    			new_parent.right = child_2;
    		} else {
    			new_parent.left = child_2;
    			new_parent.right = child_1;
    		}
    		
    		if (new_parent.str.charAt(0) == '0') {
    			prev.left = new_parent;
    		} else {
    			prev.right = new_parent;
    		}
    	}
    	
    	count ++;
    	return true;
    }

    /**
     * Deletes key from the trie.
     *
     * @param key The {@link String}  key to be deleted.
     * @return {@code true} if and only if key was contained by the trie before we attempted deletion, {@code false} otherwise.
     */
    public boolean delete(String key) {
        if (count == 0) {
    		return false;
    	}
    	String curr_str = key;
    	TrieNode curr_node = root, prev = root;
    	boolean isleft = false;
    	
    	while (curr_node != null && !curr_str.equals("")) {
    		if (curr_str.equals(curr_node.str) && curr_node.isKey) {
    			break;
    		}
    		
	    	int len = common_prefix(curr_str, curr_node.str);
	    	if (len < curr_node.str.length()) {
	    		return false;
	    	}
	    	curr_str = curr_str.substring(len);    		
    		
	    	if (curr_str.length() == 0) {
	    		return false;
	    	}
	    	
	    	prev = curr_node;
    		if (curr_str.charAt(0) == '0') {
    			curr_node = curr_node.left;
    			isleft = true;
    		} else {
    			curr_node = curr_node.right;
    			isleft = false;
    		}
    		
    	}
        
        if (curr_node == null) {
        	return false;
        }
        
        if (curr_node.left == null && curr_node.right == null) {
        	if (isleft) {
        		prev.left  = null;
        	} else {
        		prev.right = null;
        	}
        	
        	if (!prev.isKey && (prev.left == null || prev.right == null) && !prev.str.equals("")) {
        		if (prev.left != null) {
    				prev.str = prev.str.concat(prev.left.str);
    				prev.isKey = prev.left.isKey;      
    				TrieNode temp1 = prev.left.left, temp2 = prev.left.right;
    				prev.left = temp1;
    				prev.right = temp2;
            	} else if (prev.right != null) {
            		prev.str = prev.str.concat(prev.right.str);
            		prev.isKey = prev.right.isKey;
            		TrieNode temp1 = prev.right.left, temp2 = prev.right.right;
            		prev.left = temp1;
            		prev.right = temp2;
            	}
        	}
        	count --; 
        	return true;
        } else if (curr_node.left != null && curr_node.right != null) {
        	curr_node.isKey = false;
        	count --;
        	return true;
        } else {
        	if (curr_node.left != null) {
        		curr_node.left.str = curr_node.str.concat(curr_node.left.str);
        		curr_node = curr_node.left;
        	} else {
        		curr_node.right.str = curr_node.str.concat(curr_node.right.str);
        		curr_node = curr_node.right;
        	}
        	
        	if (isleft) {
    			prev.left = curr_node;    			
    		} else {
    			prev.right = curr_node;
    		}
        	count --;
        	return true;
        }
    }

    /**
     * Queries the trie for emptiness.
     *
     * @return {@code true} if and only if {@link #getSize()} == 0, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return (count == 0);
    }

    /**
     * Returns the number of keys in the tree.
     *
     * @return The number of keys in the tree.
     */
    public int getSize() {
    	return count;
    }

    /**
     * <p>Performs an <i>inorder (symmetric) traversal</i> of the Binary Patricia Trie. Remember from lecture that inorder
     * traversal in tries is NOT sorted traversal, unless all the stored keys have the same length. This
     * is of course not required by your implementation, so you should make sure that in your tests you
     * are not expecting this method to return keys in lexicographic order. We put this method in the
     * interface because it helps us test your submission thoroughly and it helps you debug your code! </p>
     *
     * <p>We <b>neither require nor test </b> whether the {@link Iterator} returned by this method is fail-safe or fail-fast.
     * This means that you  do <b>not</b> need to test for thrown {@link java.util.ConcurrentModificationException}s and we do
     * <b>not</b> test your code for the possible occurrence of concurrent modifications.</p>
     *
     * <p>We also assume that the {@link Iterator} is <em>immutable</em>, i,e we do <b>not</b> test for the behavior
     * of {@link Iterator#remove()}. You can handle it any way you want for your own application, yet <b>we</b> will
     * <b>not</b> test for it.</p>
     *
     * @return An {@link Iterator} over the {@link String} keys stored in the trie, exposing the elements in <i>symmetric
     * order</i>.
     */
    public Iterator<String> inorderTraversal() {
        return new PatriciaIterator<String>(root, count);
    }
    
    class PatriciaIterator<S> implements Iterator<String> {
    	int index;
    	ArrayList<String> list;
    	
    	// Helper to fill a list of TrieNodes that are InOrder Traversed
    	private void traverse_rec(TrieNode curr_node, String acc) {
    		if (curr_node == null) {
    			return;
    		} 
    		
    		acc = ((String) acc).concat(curr_node.str);
    		if (curr_node.left == null && curr_node.right == null) {
    			list.add(acc);
    			return;
    		} 
    		
    		traverse_rec(curr_node.left, acc);
    		if (curr_node.isKey) {
    			list.add(acc);
    		} 
			traverse_rec(curr_node.right, acc);
    	}
    	
    	// Makes an InOrder list of all TrieNodes in Trie and iterates through that
    	public PatriciaIterator(TrieNode r, int c) {
    		list = new ArrayList<String>();
    		index = 0;
    		String acc = (String) "";
    		
    		if (c != 0) {
    			traverse_rec(root, acc);
    		}
    	}
    	
    	public boolean hasNext() {
    		return (index < list.size());
    	}
    	
		public String next() {
    		if (list.size() == 0) {
    			return null;
    		}
    		
    		String result = list.get(index);
    		index ++;
    		return result;
    	}
    	
    	
    }

    /**
     * Finds the longest {@link String} stored in the Binary Patricia Trie.
     * @return <p>The longest {@link String} stored in this. If the trie is empty, the empty string &quot;&quot; should be
     * returned. Careful: the empty string &quot;&quot;is <b>not</b> the same string as &quot; &quot;; the latter is a string
     * consisting of a single <b>space character</b>! It is also <b>not the same as the</b> null <b>reference</b>!</p>
     *
     * <p>Ties should be broken in terms of <b>value</b> of the bit string. For example, if our trie contained
     * only the binary strings 01 and 11, <b>11</b> would be the longest string. If our trie contained
     * only 001 and 010, <b>010</b> would be the longest string.</p>
     */
    public String getLongest() {
    	Iterator iter = inorderTraversal();
    	String max = "", curr = "";
    	while (iter.hasNext()) { 
    		curr = (String) iter.next();
    		if (curr.length() >= max.length()) {
    			max = curr;
    		}
    	}
    	return max;
    }
    
    /**
     * Makes sure that your trie doesn't have splitter nodes with a single child. In a Patricia trie, those nodes should
     * be pruned.
     * @return {@code true} iff all nodes in the trie either denote stored strings or split into two subtrees, {@code false} otherwise.
     */
    public boolean isJunkFree(){
        return isEmpty() || (isJunkFree(root.left) && isJunkFree(root.right));
    }

    private boolean isJunkFree(TrieNode n){
        if(n == null){   // Null subtrees trivially junk-free
            return true;
        }
        if(!n.isKey){   // Non-key nodes need to be strict splitter nodes
            return ( (n.left != null) && (n.right != null) && isJunkFree(n.left) && isJunkFree(n.right) );
        } else {
            return ( isJunkFree(n.left) && isJunkFree(n.right) ); // But key-containing nodes need not.
        }
    }
}














