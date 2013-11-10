package fruit.sim;

import fruit.sim.*;

class PlayTask implements Runnable
{
    // the player
    Player player;
    // arguments
    int[] bowl;
    int bowlid;
    int round;
    boolean canPick;
    boolean mustTake;

    // result
    boolean take;

    // save exception
    Exception exception;

    public PlayTask(Player player, int[] bowl, int bowlid, int round,
                    boolean canPick, boolean mustTake) {
        this.player = player;
        this.bowl = bowl;
        this.bowlid = bowlid;
        this.round = round;
        this.canPick = canPick;;
        this.mustTake = mustTake;
    }

    public void run() {
        try {
            take = player.pass(bowl,bowlid,round,canPick,mustTake);
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        }
    }
}
