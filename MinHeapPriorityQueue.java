package pqueue.priorityqueues; // ******* <---  DO NOT ERASE THIS LINE!!!! *******


/* *****************************************************************************************
 * THE FOLLOWING IMPORTS WILL BE NEEDED BY YOUR CODE, BECAUSE WE REQUIRE THAT YOU USE
 * ANY ONE OF YOUR EXISTING MINHEAP IMPLEMENTATIONS TO IMPLEMENT THIS CLASS. TO ACCESS
 * YOUR MINHEAP'S METHODS YOU NEED THEIR SIGNATURES, WHICH ARE DECLARED IN THE MINHEAP
 * INTERFACE. ALSO, SINCE THE PRIORITYQUEUE INTERFACE THAT YOU EXTEND IS ITERABLE, THE IMPORT OF ITERATOR
 * IS NEEDED IN ORDER TO MAKE YOUR CODE COMPILABLE. THE IMPLEMENTATIONS OF CHECKED EXCEPTIONS
 * ARE ALSO MADE VISIBLE BY VIRTUE OF THESE IMPORTS.
 ** ********************************************************************************* */

import pqueue.exceptions.*;
import pqueue.heaps.ArrayMinHeap;
import pqueue.heaps.EmptyHeapException;
import pqueue.heaps.LinkedMinHeap;
import pqueue.heaps.MinHeap;

import java.util.Iterator;
import java.util.LinkedList;
/**
 * <p>{@link MinHeapPriorityQueue} is a {@link PriorityQueue} implemented using a {@link MinHeap}.</p>
 *
 * <p>You  <b>must</b> implement the methods of this class! To receive <b>any credit</b> for the unit tests
 * related to this class, your implementation <b>must</b> use <b>whichever</b> {@link MinHeap} implementation
 * among the two that you should have implemented you choose!</p>
 *
 * @author  ---- Shane Purnell ----
 *
 * @param <T> The Type held by the container.
 *
 * @see LinearPriorityQueue
 * @see MinHeap
 * @see PriorityQueue
 */
public class MinHeapPriorityQueue<T> implements PriorityQueue<T>{

	/* ***********************************************************************************
	 * Write any private data elements or private methods for MinHeapPriorityQueue here...*
	 * ***********************************************************************************/
	private class pqNode implements Comparable<pqNode>{
		private int priority, called;
		private T data; 
		
		public pqNode(int prio, T element, int call) {
			priority = prio;
			data = element;
			called = call;
		}
		
		public int compareTo(pqNode right) {
			if (this.priority < right.priority) {
				return -1;
			} else if (right.priority < this.priority) {
				return 1;
			} else {
				if (this.called < right.called) {
					return -1;
				} else if (right.called < this.called) {
					return 1;
				} else {
					return 0;
				}
			}
		}
		
	}

	//MinHeapPQ instance variables
	private ArrayMinHeap heap;
	private int called;
	
	/* *********************************************************************************************************
	 * Implement the following public methods. You should erase the throwings of UnimplementedMethodExceptions.*
	 ***********************************************************************************************************/
		/**
	 * Simple default constructor.
	 */
	public MinHeapPriorityQueue(){
		heap = new ArrayMinHeap();
		called = 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void enqueue(T element, int priority) throws InvalidPriorityException {	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if (priority <= 0) {
			throw new InvalidPriorityException("Priority must be >= 1");
		}
		
		pqNode node = new pqNode(priority, element, called);
		called ++;
		heap.insert(node);
	}

	@Override
	public T dequeue() throws EmptyPriorityQueueException {		// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if (heap.size() == 0) {
			throw new EmptyPriorityQueueException("Queue is empty");
		}
		
		pqNode node;
		try {
			node = (MinHeapPriorityQueue<T>.pqNode) heap.deleteMin();
		} catch (EmptyHeapException e){
			return null;
		} 
		
		return node.data;
	}

	@Override
	public T getFirst() throws EmptyPriorityQueueException {	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if (heap.size() == 0) {
			throw new EmptyPriorityQueueException("Queue is empty");
		}
		
		//T res = null;
		pqNode node;
		try {
			node = (MinHeapPriorityQueue<T>.pqNode) heap.getMin();
		} catch (EmptyHeapException e) {
			return null;
		}
		
		return node.data;
	}

	@Override
	public int size() {
		return heap.size();
	}

	@Override
	public boolean isEmpty() {
		if (heap.size() < 1) {
			return true;
		} else {
			return false;
		}
	}
	
	class MinHeapPQIter<T> implements Iterator<T> {
		private Iterator iter;
		private int left;
		
		public MinHeapPQIter() {
			iter = heap.iterator();
			left = heap.size();
		}
		
		public boolean hasNext() {
			if (left == 0) {
				return false;
			} else {
				return true;
			}
			//return iter.hasNext();
		}
		
		public T next() {
			MinHeapPriorityQueue<T>.pqNode node = (MinHeapPriorityQueue<T>.pqNode)iter.next();
			left --;
			return node.data;
		}
	}

	@Override
	public Iterator<T> iterator() {
		return new MinHeapPQIter<T>();
	}

}


























