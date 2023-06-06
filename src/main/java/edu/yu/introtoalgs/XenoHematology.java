package edu.yu.introtoalgs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/** Defines the API for the XenoHematology assignment: see the requirements
 * document for more information.
 *
 * Students MAY NOT change the constructor signature.  My test code will only
 * invoke the API defined below.
 *
 * @author Avraham Leff
 */

public class XenoHematology {
    private int count;
    private int[]xenos;
    private HashMap<Integer,Integer> canonicalIDToIncompatibles;
    private int[] size;
    /** Constructor: specifies the size of the xeno population.
     *
     * @param populationSize a non-negative integer specifying the number of
     * aliens in the xeno population.  Members of the population are uniquely
     * identified by an integer 0..populationSize -1.
     */
    public XenoHematology(final int populationSize) {
        this.count=populationSize;
        this.xenos=new int[populationSize];
        this.size=new int[populationSize];
        this.canonicalIDToIncompatibles=new HashMap<>();
        for(int i=0;i<populationSize;i++){
            xenos[i]=i;
            size[i]=1;
        }
        // fill me in!
    } // constructor
    public void printCurrentState(){
        System.out.println("printing array values");
        for (int i=0;i<this.xenos.length;i++){
            System.out.print(xenos[i]+" ");
        }
        System.out.println("\n");
        System.out.println("Printing lists of incompat");
        for (Integer i: canonicalIDToIncompatibles.keySet()){
            System.out.println("Xeno: "+i);
            System.out.println("incompatible with: "+canonicalIDToIncompatibles.get(i));
        }
    }
    private int find(int p){
        while(p!=xenos[p]){
            xenos[p] = xenos[xenos[p]];
            p=xenos[p];
        }
        return p;
    }
    private void union(int p,int q){
        //System.out.println();
        //System.out.println("entering union of p: "+p+" and q: "+q);
        int root1=find(p);
        int root2=find(q);
        //System.out.println("performed finds on each");
        if(root1==root2){
            return;
        }
        //making root2 the biggest
        //wanna try the smallest size bc each xeno should have at most 1 incompatible canonical- if more, they would've been unioned
        if(this.size[root1]>=this.size[root2]){
            int temp=root1;
            root1=root2;
            root2=temp;
        }
        // link root of smaller tree
        // to root of larger tree
        this.xenos[root1]=root2;
        this.size[root2]+=this.size[root1];

        //add root1 to root2 and change its incompatible's incompatibles canonicals in hashmap
        if(canonicalIDToIncompatibles.get(root1)!=null&&canonicalIDToIncompatibles.get(root2)==null){
            canonicalIDToIncompatibles.put(root2,canonicalIDToIncompatibles.get(root1));
            canonicalIDToIncompatibles.put(canonicalIDToIncompatibles.get(root1),root2);
            canonicalIDToIncompatibles.remove(root1);
        }
        //if both have incompatibles, need to union all of the incompatibles together and then make it incompatible with the canonical, root2
        else if(canonicalIDToIncompatibles.get(root1)!=null&&canonicalIDToIncompatibles.get(root2)!=null){
            int incompR1=canonicalIDToIncompatibles.get(root1); //8 in example
            int incompR2=canonicalIDToIncompatibles.get(root2); //4 in example
            //now union smaller to bigger
            if(this.size[incompR1]>=this.size[incompR2]){
                int temp=incompR1;
                incompR1=incompR2;
                incompR2=temp; //incompR2 is now 8
            }
            this.xenos[incompR1]=incompR2;
            this.size[incompR2]+=this.size[incompR1];
            //now deal with incomp canonicals in map, setting incomp of incompR2 to root2 and vice versa
            canonicalIDToIncompatibles.put(incompR2,root2);
            canonicalIDToIncompatibles.put(root2,incompR2);
        }
        //System.out.println("exiting union\n");
    }
    /** Specifies that xeno1 and xeno2 are incompatible.  Once specified
     * as incompatible, the pair can never be specified as being
     * "compatible".  In that case, don't throw an exception, simply
     * treat the method invocation as a "no-op".  A xeno is always
     * compatible with itself, is never incompatible with itself:
     * directives to the contrary should be treated as "no-op"
     * operations.
     *
     * Both parameters must correspond to a member of the population.
     *
     * @param xeno1 non-negative integer that uniquely specifies a member of the
     * xeno population, differs from xeno2
     * @param xeno2 non-negative integer that uniquely specifies a member of the
     * xeno population.
     * @throws IllegalArgumentException if the supplied values are incompatible
     * with the above semantics or those specified by the requirements doc.
     */
    //max uses 2 finds+ iteration of incompatibles of both xeno 1 and xeno 2 as well as subsequent incompatibles, and unions on all for those
    //in other words for total amount of incompatibles found and added which we will call z, it uses z iterations, doing a union in each for a total of z unions
    //each of which iterates over the incompatibles of the elements being added
    public void setIncompatible(int xeno1, int xeno2) {
        if(xeno1<0||xeno2<0||xeno1>count-1||xeno2>count-1){
            throw new IllegalArgumentException("issue with xeno identifiers given");
        }
        if(xeno1==xeno2||areCompatible(xeno1,xeno2)){
            return;
        }
        //adds xeno2 to xeno1's list of incompatibles and union xeno1 with all elements xeno2 is incompatible with
        int canonX1=find(xeno1);
        int canonX2=find(xeno2);
        // if both have no incompats
        if(canonicalIDToIncompatibles.get(canonX1)==null&&canonicalIDToIncompatibles.get(canonX2)==null){
            canonicalIDToIncompatibles.put(canonX1,canonX2);
            canonicalIDToIncompatibles.put(canonX2,canonX1);
        }
        //if cX1 has, and cX2 doesn't
        else if(canonicalIDToIncompatibles.get(canonX1)!=null&&canonicalIDToIncompatibles.get(canonX2)==null){
            this.union(canonicalIDToIncompatibles.get(canonX1),canonX2);
        }
        //if cX2 has, and cX1 doesn't
        else if(canonicalIDToIncompatibles.get(canonX2)!=null&&canonicalIDToIncompatibles.get(canonX1)==null){
            this.union(canonicalIDToIncompatibles.get(canonX2),canonX1);
        }
        //if both have incompats
        else{
            this.union(canonX1,canonicalIDToIncompatibles.get(canonX2));
        }
    }

    /** Specifies that xeno1 and xeno2 are compatible.  Once specified
     * as compatible, the pair can never be specified as being
     * "incompatible".  In that case, don't throw an exception, simply
     * treat the method invocation as a "no-op".  A xeno is always
     * compatible with itself, is never incompatible with itself:
     * directives to the contrary should be treated as "no-op"
     * operations.
     *
     * Both parameters must correspond to a member of the population.
     *
     * @param xeno1 non-negative integer that uniquely specifies a member of the
     * xeno population.
     * @param xeno2 non-negative integer that uniquely specifies a member of the
     * xeno population
     * @throws IllegalArgumentException if the supplied values are incompatible
     * with the above semantics or those specified by the requirements doc.
     */
    //max uses 1 find, iteration across list of xeno1 incompatibles and a union
    public void setCompatible(int xeno1, int xeno2) {
        if(xeno1<0||xeno2<0||xeno1>count-1||xeno2>count-1){
            throw new IllegalArgumentException("issue with xeno identifiers given");
        }
        if(xeno1==xeno2||areIncompatible(xeno1,xeno2)){
            return;
        }
        this.union(xeno1,xeno2);
    }

    /** Returns true iff xeno1 and xeno2 are compatible from a hematology
     * perspective, false otherwise (including if we don't know one way or the
     * other).  Both parameters must correspond to a member of the population.
     *
     * @param xeno1 non-negative integer that uniquely specifies a member of the
     * xeno population, differs from xeno2
     * @param xeno2 non-negative integer that uniquely specifies a member of the
     * xeno population
     * @return true iff compatible, false otherwise
     * @throws IllegalArgumentException if the supplied values are incompatible
     * with the above semantics or those specified by the requirements doc.
     */
    //max uses 2 finds
    public boolean areCompatible(int xeno1, int xeno2) {
        if(xeno1<0||xeno2<0||xeno1>count-1||xeno2>count-1){
            throw new IllegalArgumentException("issue with xeno identifiers given");
        }
        else{
            return find(xeno1)==find(xeno2);
        }
    }

    /** Returns true iff xeno1 and xeno2 are incompatible from a hematology
     * perspective, false otherwise (including if we don't know one way or the
     * other).  Both parameters must correspond to a member of the population.
     *
     * @param xeno1 non-negative integer that uniquely specifies a member of the
     * xeno population, differs from xeno2
     * @param xeno2 non-negative integer that uniquely specifies a member of the
     * xeno population
     * @return true iff compatible, false otherwise
     * @throws IllegalArgumentException if the supplied values are incompatible
     * with the above semantics or those specified by the requirements doc.
     */
    //uses max of 1 find, +list of elements incompatible with xeno 1(worst case will b n-1 if had n-1 set incompatibles, so still amortizes)
    public boolean areIncompatible(int xeno1, int xeno2) {
        if(xeno1<0||xeno2<0||xeno1>count-1||xeno2>count-1){
            throw new IllegalArgumentException("issue with xeno identifiers given");
        }
        else{
            int canonX1=find(xeno1);
            if(canonicalIDToIncompatibles.get(canonX1)==null){
                return false;
            }
            else if(canonicalIDToIncompatibles.get(canonX1)==find(xeno2)){
                return true;
            }
        }
        return false;
    }

} // XenoHematology
