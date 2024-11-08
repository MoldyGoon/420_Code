package pqueue.priorityqueues; // ******* <---  DO NOT ERASE THIS LINE!!!! *******

/* *****************************************************************************************
 * THE FOLLOWING IMPORTS ARE HERE ONLY TO MAKE THE JAVADOC AND iterator() METHOD SIGNATURE
 * "SEE" THE RELEVANT CLASSES. SOME OF THOSE IMPORTS MIGHT *NOT* BE NEEDED BY YOUR OWN
 * IMPLEMENTATION, AND IT IS COMPLETELY FINE TO ERASE THEM. THE CHOICE IS YOURS.
 * ********************************************************************************** */

import demos.GenericArrays;
import pqueue.exceptions.*;
import pqueue.fifoqueues.FIFOQueue;
import pqueue.heaps.ArrayMinHeap;

import java.util.*;
/**
 * <p>{@link LinearPriorityQueue} is a {@link PriorityQueue} implemented as a linear {@link java.util.Collection}
 * of common {@link FIFOQueue}s, where the {@link FIFOQueue}s themselves hold objects
 * with the same priority (in the order they were inserted).</p>
 *
 * <p>You  <b>must</b> implement the methods in this file! To receive <b>any credit</b> for the unit tests related to
 * this class, your implementation <b>must</b>  use <b>whichever</b> linear {@link Collection} you want (e.g
 * {@link ArrayList}, {@link LinkedList}, {@link java.util.Queue}), or even the various {@link List} and {@link FIFOQueue}
 * implementations that we provide for you. You can also use <b>raw</b> arrays, but take a look at {@link GenericArrays}
 * if you intend to do so. Note that, unlike {@link ArrayMinHeap}, we do not insist that you use a contiguous storage
 * {@link Collection}, but any one available (including {@link LinkedList}) </p>
 *
 * @param <T> The type held by the container.
 *
 * @author  ---- Shane Purnell ----
 *
 * @see MinHeapPriorityQueue
 * @see PriorityQueue
 * @see GenericArrays
 */
public class LinearPriorityQueue<T> implements PriorityQueue<T> {

	/* ***********************************************************************************
	 * Write any private data elements or private methods for LinearPriorityQueue here...*
	 * ***********************************************************************************/
	private Object[] main_arr;
	private int total, cap, realModCount;
	
	private int getFirstList() {
		for (int i = 0; i < main_arr.length; i ++) {
			if (((LinkedList<T>)main_arr[i]).size() > 0) {
				return i;
			}
		}
		
		return 0;
	}
	
	private int getNextList(int start) {
		for (int i = start + 1; i < main_arr.length; i ++) {
			if (((LinkedList<T>)main_arr[i]).size() > 0) {
				return i;
			}
		}
		
		return -1; 
	}


	/* *********************************************************************************************************
	 * Implement the following public methods. You should erase the throwings of UnimplementedMethodExceptions.*
	 ***********************************************************************************************************/

	/**
	 * Default constructor initializes the element structure with
	 * a default capacity. This default capacity will be the default capacity of the
	 * underlying element structure that you will choose to use to implement this class.
	 */
	public LinearPriorityQueue(){
		int sz = 10000;
		total = 0;
		// No cap on inner element
		cap = -1;
		realModCount = 0;
		 
		main_arr = new Object[sz];
		for (int i = 0; i < sz; i ++) {
			main_arr[i] = new LinkedList<T>();
		}
	}

	/**
	 * Non-default constructor initializes the element structure with
	 * the provided capacity. This provided capacity will need to be passed to the default capacity
	 * of the underlying element structure that you will choose to use to implement this class.
	 * @see #LinearPriorityQueue()
	 * @param capacity The initial capacity to endow your inner implementation with.
	 * @throws InvalidCapacityException if the capacity provided is less than 1.
	 */
	public LinearPriorityQueue(int capacity) throws InvalidCapacityException{	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if (capacity < 1) {
			throw new InvalidCapacityException("Capacity must be >= 1");
		}
		total = 0;
		cap = capacity;
		realModCount = 0;
		main_arr = new Object[10000];
		
		for (int i = 0; i < 10000; i ++) {
			main_arr[i] = new LinkedList<T>();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void enqueue(T element, int priority) throws InvalidPriorityException{	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if (priority <= 0) {
			throw new InvalidPriorityException("Priority must be >= 1");
		}
		if (cap == -1) {
			((LinkedList<T>)main_arr[priority]).add(element);
			total++;
			realModCount ++;
			return;
		}
		
		if (((LinkedList<T>)main_arr[priority]).size() >= cap) {
			realModCount ++;
			return;
		} else {
			((LinkedList<T>)main_arr[priority]).add(element);
			realModCount ++;
			total ++;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T dequeue() throws EmptyPriorityQueueException { 	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		int ind = getFirstList();
		if (total == 0 || ind == 0) {
			throw new EmptyPriorityQueueException("Queue is empty");
		}
		
		if (ind <= 0) {
			return null;
		}
	
		T res = ((LinkedList<T>)main_arr[ind]).getFirst();
		((LinkedList<T>)main_arr[ind]).remove();
		realModCount ++;
		total --;
		return res;
	}

	@Override
	public T getFirst() throws EmptyPriorityQueueException {	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		int ind = getFirstList();
		if (total == 0 || ind == 0) {
			throw new EmptyPriorityQueueException("Queue is empty");
		}
	
		T res = ((LinkedList<T>)main_arr[ind]).getFirst();
		return res;
	}

	@Override
	public int size() {
		return total;
	}

	@Override
	public boolean isEmpty() {
		if (total == 0) {
			return true;
		} else {
			return false;
		}
	}

	class LinearPQIter<T> implements Iterator<T> {
		int curr_outer, curr_inner, expected, numNodes;
		
		LinearPQIter(int out, int in, int inModCount) {
			curr_outer = out;
			curr_inner = in;
			expected = inModCount;
			numNodes = total;
		}
		
		public boolean hasNext() {
			if (numNodes <= 0) {
				return false;
			} else {
				return true;
			}
//			if (total == 0) {
//				return false;
//			}
//			int nex = getNextList(curr_outer);
//			boolean next_in = (curr_inner) > ((LinkedList<T>)main_arr[curr_outer]).size() - 1;
//			
//			if (nex == -1 && next_in) {
//				return false;
//			} else {
//				return true;
//			}
		}
		
		public T next() {
			if (expected != realModCount) {
				throw new java.util.ConcurrentModificationException();
			}
			
			if (total == 0) {
				return null;
			}
			T res = ((LinkedList<T>)main_arr[curr_outer]).get(curr_inner);
			if (curr_inner + 1 > ((LinkedList<T>)main_arr[curr_outer]).size() - 1) {// || ((LinkedList<T>)main_arr[curr_outer]).get(curr_inner + 1) == null) {
				curr_inner = 0;
				curr_outer = getNextList(curr_outer);
			} else {
				curr_inner ++;
			}
			
			numNodes --;
			return res;
			
		}
		
	}
	
	@Override
	public Iterator<T> iterator() {
		int i = getFirstList();
		return new LinearPQIter<T>(i, 0, realModCount);
	}

}



































