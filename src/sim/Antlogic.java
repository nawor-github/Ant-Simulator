package sim;

import org.joml.Vector3f;

import sceneObjects.Ant;

public class Antlogic extends Thread {
	private String task;
	private Ant a;
	private int i;
	private float deltaTime;
	
	public Antlogic(String task, Ant ant, int antIndex, float deltaTime) {
        this.task = task;
        this.a = ant;
        this.i = antIndex;
        this.deltaTime = deltaTime;
    }

    public void run() {
        System.out.println(task + " is being prepared by " + Thread.currentThread().getName() + " which is " + Thread.currentThread().getState());
        calcMovement();
        a.incrementCounter();
        
        
    }
    
    private void calcMovement() {
		//Square current = getCurrentSquare(i);
		
		Vector3f newPos = new Vector3f(a.heading[i].x * a.MOVE_SPEED * deltaTime, a.heading[i].y * a.MOVE_SPEED * deltaTime, 1f);
		newPos.x += a.position[i].x;
		newPos.y += a.position[i].y;
		
		Square next = a.grid.getSquareAtWorldPos(newPos);
		
		float turnMult = a.turnDirection(i);
		a.rotation[i].x += (turnMult + (a.RANDOM_WIGGLE*Scene.randBetween(-1,1))) * a.TURN_SPEED * deltaTime;

		a.heading[i] = a.calcHeading(a.rotation[i].x);
		
		if (!next.isBlocker) {
			a.position[i].x = newPos.x;
			a.position[i].y = newPos.y;
		}
		
		a.leftAntennaeBalls.position[i] = a.leftPos.get(i);
		a.rightAntennaeBalls.position[i] = a.rightPos.get(i);
		a.foodBalls.position[i] = a.frontPos.get(i);
	}
}
