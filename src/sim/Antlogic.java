package sim;

public class Antlogic extends Thread {
	private String task;
	
	public Antlogic(String task) {
        this.task = task;
    }

    public void run() {
        System.out.println(task + " is being prepared by " +
            Thread.currentThread().getName() + " which is " + Thread.currentThread().getState());
    }
}
