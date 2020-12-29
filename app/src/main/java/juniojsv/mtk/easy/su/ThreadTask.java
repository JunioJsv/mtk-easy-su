package juniojsv.mtk.easy.su;

/* source : https://calvinjmkim.tistory.com/35  */
/* Warning : synchronization is not considered.
             When multiple ThreadTasks are running, if multiple threads access shared resources, synchronization is not possible.
             This needs to be synchronized to the part that accesses the resource, and so on.
 */

public abstract class ThreadTask<T1> implements Runnable {

    // Result
    T1 mResult;

    // Execute
    final public void execute() {
        // Call onPreExecute
        onPreExecute();

        // Begin thread work
        Thread thread = new Thread(this);
        thread.start();

        // Wait for the thread work
        try {
            thread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            onPostExecute(null);
            return;
        }

        // Call onPostExecute
        onPostExecute(mResult);
    }

    @Override
    public void run() {
        mResult = doInBackground();
    }

    // onPreExecute
    protected abstract void onPreExecute();

    // doInBackground
    // doInBackground runs inside a worker thread created by execute().
    // Can't run ui processing during doInBackground; use Activity.runOnUIThread() instead
    protected abstract T1 doInBackground();

    // onPostExecute
    protected abstract void onPostExecute(T1 result);
}
