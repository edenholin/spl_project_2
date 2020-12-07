package bgu.spl.mics.application.services;
import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
	private Future[] futures;
	private AttackEvent[] attackEvents;
	
    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
        // -- for each attack store it's future --
        this.futures = new Future[attacks.length];
        // -- for each attack create Attack event --
        attackEvents = new AttackEvent[attacks.length];
        for (int i = 0; i <attacks.length ; i++) {
            attackEvents[i] = new AttackEvent(attacks[i]);
        }
        // ------------------------------------------
    }

    @Override
    protected void initialize() {
        MessageBusImpl messageBus = MessageBusImpl.getInstance();
        // -- wait for attackers to subscribe --
        try {
            Thread.sleep(5000); // 5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // -------------------------------------
        // --- send attacks ---
        for (int i = 0; i < attackEvents.length; i++) {
            futures[i] = messageBus.sendEvent(attackEvents[i]);
        }
        // -- wait for attacks to finish --
        for(Future future : futures){
            future.get(); // blocking until attack was resolved
        }
        // -- send DeactivationEvent to R2D2 --
        messageBus.sendEvent(new DeactivationEvent());
        // -- subscribe to TerminateBroadcast and terminate accordingly --
        this.subscribeBroadcast(TerminateBroadcast.class, c -> {
            Diary.getInstance().setLeiaTerminate(System.currentTimeMillis());
            this.terminate();
        });
        //------------------------------------------------------------------
    }
}
