/* LIMITEDSIZEQUEUE CODE FROM:
        http://stackoverflow.com/questions/1963806/is-there-a-fixed-sized-queue-which-removes-excessive-elements
 */

package asu.cse535.group3.project;

import android.util.Log;

import java.util.ArrayList;

public class LimitedSizeQueue<K> extends ArrayList<K> {

    private int maxSize;

    public LimitedSizeQueue(int size){
        this.maxSize = size;
    }

    public boolean add(K k){
        Log.d("size", Integer.toString(size()));

        boolean r = super.add(k);
        if (size() > maxSize){
            removeRange(0, size() - maxSize - 1);
        }
        return r;
    }

    public K getYongest() {
        return get(size() - 1);
    }

    public K getOldest() {
        return get(0);
    }
}