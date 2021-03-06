package bgu.spl.mics.application.services;

//import java.util.Collections;
//import java.util.List;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {
    private final Ewoks ewoks;
    public HanSoloMicroservice() {
        super("Han");
        this.ewoks = Ewoks.getInstance();
    }

    @Override
    protected void initialize() {
        this.subscribeEvent(AttackEvent.class, c -> {

            System.out.println("Han: I got an attack to do.."); ///////////////////////////////////////////

            Ewoks.getInstance().ResourceManager(c); // a method that sets the Attacks and Ewoks for the micro service //////////////////////
            System.out.println("Han: I finished this attack!"); ///////////////////////////////////////////

            Diary.getInstance().setHanSoloFinish(System.currentTimeMillis());
            Diary.getInstance().incrementTotalAttacks();
            this.complete(c, true);
        });
        // -- subscribe to TerminateBroadcast and terminate accordingly --
        this.subscribeBroadcast(TerminateBroadcast.class, c -> {
            Diary.getInstance().setHanSoloTerminate(System.currentTimeMillis());

            System.out.println("Han: I'm done here!"); ///////////////////////////////////////////

            this.terminate();
        });
    }

}
