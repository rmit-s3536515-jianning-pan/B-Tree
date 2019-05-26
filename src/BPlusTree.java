
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BPlusTree<K extends Comparable<? super K>, V> {
 
 public static final int BRANCH_FACTOR = 256;

 private int n;  // the max number of keys in the node
 private AbstractNode root; 
 public BPlusTree(int n) { // set the max key in the node
  this.n = n;
  root = new LeafNode(); // create a empty leaf node
 }
 public BPlusTree() {
  this.n= BRANCH_FACTOR;
  root = new LeafNode();
 }
 
 //equality search
 public void search(K k) {
  AbstractNode node = root;
  while (node instanceof BPlusTree.InternalNode) { // need to traverse down to the leaf
InternalNode inner = (InternalNode) node;
int idx = -1;
System.out.println(inner.keys.size() + " " +inner.keys);
  for(int i=0;i<inner.keys.size();i++) {
   if(inner.keys.get(i).compareTo(k)>0) {
    idx = i;
    System.out.println("Inside the search" + idx + " " + inner.keys.get(idx));
    break;
   }
  }

  int childIndex = idx ==-1 ? inner.keys.size() : idx;
   node = inner.children.get(childIndex);
           
  }

  //We are @ leaf after while loop
  LeafNode leaf = (LeafNode) node;
  int idx = leaf.getLoc(k);
  if(idx==leaf.keys.size()) {
   System.out.println("Not found");
  } else {
     int count = 1;
   while(idx<leaf.keys.size() && leaf.keys.get(idx).equals(k)) {
    System.out.println(leaf.keys.get(idx) + " index " + idx);
    System.out.println(leaf.values.get(idx));
    idx++;
    count++;
    if(idx==leaf.keys.size()) {
     leaf=leaf.next;
     idx=0;
    }
    
    if(leaf==null) break;
   }
   System.out.println("There are " + (count-1) + " elements");
  
  }
  
 }
 
 public void rangeSearch(K from, K to) {
  AbstractNode node = root;
  while (node instanceof BPlusTree.InternalNode) { // need to traverse down to the leaf
InternalNode inner = (InternalNode) node;
int idx = -1;
System.out.println(inner.keys.size() + " " +inner.keys);
  for(int i=0;i<inner.keys.size();i++) {
   if(inner.keys.get(i).compareTo(from)>0) {
    idx = i;
    System.out.println("Inside the search" + idx + " " + inner.keys.get(idx));
    break;
   }
  }
  int childIndex = idx ==-1 ? inner.keys.size() : idx;

           node = inner.children.get(childIndex);
           
  }

  //We are @ leaf after while loop
  LeafNode leaf = (LeafNode) node;
  int idx = leaf.getLoc(from);
  if(idx==leaf.keys.size()) {
   System.out.println("Not found");
  } else {
     int count = 1;
   while(idx<leaf.keys.size() && (leaf.keys.get(idx).compareTo(from) >=0  && leaf.keys.get(idx).compareTo(to)<=0)) {
    System.out.println(leaf.keys.get(idx) + " index " + idx);
    System.out.println(leaf.values.get(idx));
    idx++;
    count++;
    if(idx==leaf.keys.size()) {
     leaf=leaf.next;
     idx=0;
    }
    
    if(leaf==null) break;
   }
   System.out.println("There are " + (count-1) + " elements");
  
  }
 }
 
 /*
  * Insert key and value
  */
 public void insertValue(K k,V v) { 
//  System.out.println("Insert Key = " + k + " , Value = " +v );
   Split result = root.insertValue(k, v); // first time insert, since it is leaf node, split until it is full
   if(result !=null) { // it is split , create a new Internal Node instead Lead Node , cause the level goes up
    InternalNode newRoot = new InternalNode(); //since it is splited , we need to create new root
    newRoot.keys.add(result.pivotPoint); // add the pivot point to the new root
    newRoot.children.add(result.left); // add the left pointer to the new root
    newRoot.children.add(result.right); // add the right pointer to the new root

    //modify the old root to new root
    root = newRoot;

   }
 }
 
 
 
 
 // abstract class for node , node has INode and LeafNode , has all the common variables and functions
 abstract class AbstractNode{
  List<K> keys; 
  
  
  abstract int getLoc(K k);
  abstract Split insertValue(K k,V v);
  abstract V getV(K k);
  
  abstract Split split();
  abstract boolean isOverflow();
  abstract void insertEntry(K key, V value);
 }
 
 // Leaf Node store key and value
 class LeafNode extends AbstractNode{
  private List<V> values;
  private LeafNode next;
  
  public LeafNode() {
   // TODO Auto-generated constructor stub
   this.keys = new ArrayList<>();
   this.values = new ArrayList<>();
   next =null;
  }
  @Override
  Split insertValue(K k, V v) {
   // TODO Auto-generated method stub
   
   if(this.isOverflow()) { // it is full already , need to split
     Split result = this.split();
     System.out.println(result.pivotPoint);
     if(k.compareTo(result.pivotPoint) < 0 ) { // if current key is less than pivot point
       
        this.insertEntry(k, v); // insert to the left node
     }
     else {  // key is greater and equal to the pivot point
      System.out.println("before " + result.right.keys + " " + result.pivotPoint);
       result.right.insertEntry(k, v); // insert to the right node
       
       System.out.println(result.right.keys + " " + result.pivotPoint);
     }
     
     return result;
   }
   else { //if it is not full, do not split
    this.insertEntry(k, v); // insert to the current leaf 
   }
   return null;
  }

  void insertEntry(K key, V value) { // Not null and if we want to insert a new key and value , need to find the key less than the provided
    int idx = getLoc(key); //
    int valueIndex = idx >= 0 ? idx : -idx - 1;
    if(idx >=0) { // if the key in the index is equal to "key", it means duplicate key and value

        keys.add(idx, key);
        values.add(idx, value);
        
    }
    else { // it is unique key and value , it is not found
     
      // the add function , insert the element and push all other elements down , NOT Override 
      keys.add(valueIndex,key);
      values.add(valueIndex, value);
    }
    System.out.println("In the LeafNode , Insert Key = " + key + " , Value = " +value );

  }
  
  @Override
  V getV(K k) {
   // TODO Auto-generated method stub
   int idx = getLoc(k);
   if(idx >=0) {
    
    return values.get(idx);
   }
   else 
    return null;

  }

  @Override
  Split split() {
   // TODO Auto-generated method stub
   Split result = new Split(); // create split for old leaf and new leaf
   LeafNode sibling = new LeafNode(); // create a new leaf node because the old node is full
   int mid =(n+1)/2; // the pivot point for the split
   sibling.keys.addAll(keys.subList(mid, keys.size())); // take the key from mid to the end of list and insert to the new node
   sibling.values.addAll(values.subList(mid, keys.size())); // take the values
   
   //clear the key and value from mid to the end of list, because already insert to another new node
   keys.subList(mid, keys.size()).clear();
   values.subList(mid, keys.size()).clear();

   sibling.next = next; // here : next is null, so sibling.next is null
   next = sibling; // set the current leaf to the new sibling;
   
   result.pivotPoint = sibling.keys.get(0); 
   result.left = this;
   result.right = sibling;

   return result;
  }

  @Override
  boolean isOverflow() {
   // TODO Auto-generated method stub
   if(keys.size()==n) {
    return true;
   }
   return false;
  }
  
  /*
   * return the index if key is found in the list, 
   * otherwise , return 0 or -1 
   */
  @Override
  int getLoc(K k) {
   // TODO Auto-generated method stub
   int loc =-1;
   for(int i=0;i<keys.size();i++) {
    if(keys.get(i).compareTo(k)>=0) {
     loc = i;
     break;
    }
   }
   if(loc==-1) {
    return keys.size();
   }
   else return loc;

  }
  
 }
 
 
 
 // Internal Node should have keys and pointer ( here is Class itself
 class InternalNode extends AbstractNode{
  
  List<AbstractNode> children; // in the internal node , it has pointers to child
  
  public InternalNode() {
   keys = new ArrayList<>();
   children = new ArrayList<>();
  }
  
  public InternalNode(K k, AbstractNode... nodes) {
   keys = new ArrayList<>();
   children = new ArrayList<>();
   keys.add(k);
   for(AbstractNode node : nodes) {
    children.add(node);
   }
  }
  
  @Override
  Split insertValue(K k, V v) {
   // TODO Auto-generated method stub
   AbstractNode subtree = this.choose_subtree(k);
   Split s = subtree.insertValue(k, v);
   if(s!=null) { // if the child splits
     if(this.isOverflow()) { // internal node needs to split
      Split result = this.split();
//      result.pivotPoint = k;
      InternalNode newRoot = new InternalNode();
      newRoot.keys.add(result.pivotPoint);
      newRoot.children.add(this);
      newRoot.children.add(new InternalNode(s.pivotPoint, s.left,s.right));
//      InternalNode sibling = new InternalNode();
//      sibling.keys
      root = newRoot;
      

     }
     else {
      this.insertEntry((K)s.pivotPoint , (V)s.right); // ok
      return null;
     }
    
   } // child has no split
   
    return null;
  }
  
  void insertEntry(K key, V value) {
   int idx = getLoc(key); // if >=0 it is found
   System.out.println("Internal " + idx + " KEYS " + key);
   int valueIndex = idx >= 0 ? idx : -idx - 1;
   System.out.println("internal idx" + idx + " " + valueIndex);
   if(idx>=0) { //same key,  but with different value
    keys.add(idx, key);
    children.add(idx, (AbstractNode)value);
    
   }
   else {
    keys.add(valueIndex, key);
    children.add(valueIndex+1, (AbstractNode)value);
   }
   System.out.println("Internal Node , Insert Key = " + key + " , Value = " +value );

  }

  @Override
  V getV(K k) {
   // TODO Auto-generated method stub
   int idx = getLoc(k);
   int valueIndex = idx >= 0 ? idx : -idx - 1;
   AbstractNode node = children.get(valueIndex);

   return node.getV(k);

  }

  @Override
  Split split() {
   // TODO Auto-generated method stub
   
   Split result = new Split(); // create split for old leaf and new leaf
   InternalNode sibling = new InternalNode(); // create a new leaf node because the old node is full
   int mid =(n+1)/2; // the pivot point for the split
   sibling.keys.addAll(keys.subList(mid, keys.size())); // take the key from mid to the end of list and insert to the new node
   sibling.children.addAll(children.subList(mid+1, keys.size()+1)); 
   System.out.println(keys);
   System.out.println(sibling.keys + " children " + sibling.children.get(0).keys);
   
   

   //clear the key and chilren from mid to the end of list, because already insert to another new node
   keys.subList(mid, keys.size()).clear();
   children.subList(mid+1, keys.size()+1).clear();
   System.out.println(keys);
   System.out.println(sibling.keys);
   
   result.pivotPoint = sibling.keys.get(0); 
   result.left = this;
   result.right = sibling;

   return result;

  }

  @Override
  boolean isOverflow() {
   // TODO Auto-generated method stub
   if(keys.size()==n) {
    return true;
   }
   return false;  }
  
  AbstractNode choose_subtree(K k) {

   int idx = getLoc(k);
   int valueIndex = idx >= 0 ? idx : -idx - 1;
   return children.get(valueIndex);

     
  }

  @Override
  int getLoc(K k) {
   // TODO Auto-generated method stub

   int loc = Collections.binarySearch(keys, k);
    return loc;

  }
  
 }
 
 
 class Split {
   public K pivotPoint; // pivot point
   public AbstractNode left; 
   public AbstractNode right;
  
 }
 
}