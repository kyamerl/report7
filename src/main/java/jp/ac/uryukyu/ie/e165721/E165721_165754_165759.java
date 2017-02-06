package jp.ac.uryukyu.ie.e165721;

/**
 * Created by e165721 on 2017/02/06.
 */

import robocode.*;
import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * E165721_165754_165759 - a robot by (your name here)
 */
public class E165721_165754_165759 extends AdvancedRobot
{
    double mm;              // 少しだけ移動する時の長さを移動する変数
    int back_or_ahead;      // 前進，後退を交互に行うための変数
    int mode1set=0;         // 最初のモードを開始したかどうか判定する変数
    int mode2set=0;         // ２番目のモードを開始したかどうか判定する変数
    int mode3set=0;         // ３番目のモードを開始したかどうか判定する変数
    int mode4set=0;         // ４番目のモードを開始したかどうか判定する変数
    int notscan=0;          // ２番目のモードの時に
    int last_ecomode=0;     // 省エネモードに移行するか判断する変数
    int through=0;          // 無駄な回転を防ぐための変数
    int enemyNumber;        // 敵の数を入れる変数
    double moveAmount;      // 移動する長さを入れる変数
    /**
     * run: E165721_165754_165759's default behavior
     */
    public void run() {
        moveAmount = Math.max(getBattleFieldWidth(), getBattleFieldHeight());// moveAmountをバトルフィールドの最大値に設定
        mm = getBattleFieldWidth() / 10;    // mmをバトルフィールドの幅の１/１０の長さに設定
        while(true) {
            enemyNumber = getOthers();      // 敵の数を取得し代入
            if(last_ecomode>5 || getEnergy()<30 ){      // last_ecomodeの数が５以上か，エネルギーが３０以下なら省エネモードに変更
                mode4run();
            }else if(enemyNumber >= 4){
                mode1run();
            }else if(enemyNumber >= 2){
                mode2run();
            }else{
                mode3run();
            }
        }
    }



    void mode1run(){
        setColors(Color.blue,Color.blue,Color.blue);
        if(mode1set==0){
            turnLeft(getHeading() % 90);    // ロボットの現在の向きを90度で割ったあまりの分だけ左に回転
            ahead(moveAmount);  // moveAmount分だけ前進 (壁まで前進)
            turnLeft(getHeading() % 90+90); // ロボットの現在の向きを90度で割ったあまりに９０度足したの分だけ左に回転（壁に対して９０度回転）
            mode1set=1;
        }
        ahead(moveAmount);// moveAmount分だけ前進 (壁まで前進)
        turnGunLeft(360);// 砲塔を一回転させスキャンする
        through=0;
        turnLeft(getHeading() % 90+90);// ロボを90度だけ右回転
        through=1;
    }

    void mode2run(){
        if(mode2set==0)turnGunRight(90);
        mode2set=1;
        setColors(Color.yellow,Color.yellow,Color.yellow);  // モードで色分け（黄）
        if(mode1set==0){
            turnLeft(getHeading() % 90);// ロボットの現在の向きを90度で割ったあまりの分だけ左に回転
            ahead(moveAmount);// moveAmount分だけ前進 (壁まで前進)
            turnRight(getHeading() % 90+90);// ロボを90度だけ右回転
            mode1set=1;
        }
        ahead(moveAmount);  // moveAmount分だけ前進 (壁まで前進)
        back(moveAmount);   // moveAmount分だけ後退 (壁まで後退)
    }

    void mode3run(){
        setColors(Color.red,Color.red,Color.red);  // モードで色分け（赤）
        if(mode3set==0){
            if(mode2set==1){
                turnGunRight(-180);
            }else{
                turnGunRight(-90);
            }
        }
        mode3set=1;
        back_or_ahead++;
        if(back_or_ahead%2==0)ahead(mm);
        if(back_or_ahead%2==1)back(mm);
        turnRadarRight(360);
    }

    void mode4run(){
        setColors(Color.white,Color.white,Color.white);     // モードで色分け（白）
        turnLeft(45);
        ahead(30);
        if(mode3set==0){        // ３番目のモードを開始してないなら実行
            if(mode2set==1){    // ２番目のモードを開始してたら実行
                turnGunRight(-180);
            }else{
                turnGunRight(-90);
            }
        }
        mode3set=1;
        if(last_ecomode>5 || getEnergy()<30)mode4set=1;
        ahead(moveAmount);  // moveAmount分だけ前進 (壁まで前進)
        back(moveAmount);   // moveAmount分だけ後退 (壁まで後退)
    }

    /**
     * onScannedRobot: What to do when you see another robot
     */
    public void onScannedRobot(ScannedRobotEvent e) { // ロボットをスキャンしたら実行
        if(mode4set==0){
            if(enemyNumber >= 2){
                notscan=0;
                if(e.getDistance() < getBattleFieldHeight() / 4){// 敵がバトルフィールドの高さ/4の長さ以内（近く）にいれば実行
                    fire(3);
                }else{
                    fire(2);
                }

            }else{

                turnRight(e.getBearing()+90);   // 敵に対して９０度に向く
                if(e.getDistance() < getBattleFieldHeight() / 4){   // 敵がバトルフィールドの高さ/4の長さ以内（近く）にいれば実行
                    fire(3);
                }else{
                    fire(2);
                }
                back_or_ahead++;
                if(back_or_ahead%2==0)ahead(mm);
                if(back_or_ahead%2==1)back(mm);
            }
        }
    }

    /**
     * onHitByBullet: What to do when you're hit by a bullet
     */
    public void onHitByBullet(HitByBulletEvent e) {
        if(mode4set==0){
            if(enemyNumber >= 4){
                if(through==0)turnLeft(getHeading() % 90+90);   // ロボットの現在の向きを90度で割ったあまりに９０度足したの分だけ左に回転（壁に対して９０度回転）
                through=1;

            }else if(enemyNumber >= 2){
                notscan=0;
                if(e.getBearing() >= 0){
                    turnRight(getHeading() % 90+90);    // ロボットの現在の向きを90度で割ったあまりに９０度足したの分だけ右に回転（壁に対して９０度回転）
                }else{
                    turnLeft(getHeading() % 90+90);     // ロボットの現在の向きを90度で割ったあまりに９０度足したの分だけ左に回転（壁に対して９０度回転）
                }
            }else{
                turnRight(e.getBearing()+90);           // 敵に対して９０度に向く
                fire(1);
            }
        }
    }

    /**
     * onHitWall: What to do when you hit a wall
     */
    public void onHitWall(HitWallEvent e) {

        if(enemyNumber >= 2 && enemyNumber < 4){    //２番目のモードで実行
            notscan++;
            if(notscan==2)turnRight(180);           //何もスキャンせず壁に二回当たれば逆を向く
        }
    }

    public void onHitRobot(HitRobotEvent e){

        if(e.getBearing() > -90 && e.getBearing() < 90){    //ぶつかったロボットが前方にいれば実行
            back(mm);
        }else{
            ahead(mm);
        }

    }

    public void onBulletHit(BulletMissedEvent e){

        if(enemyNumber < 2)last_ecomode--;

    }

    public void onBulletMissed(BulletMissedEvent e){

        if(enemyNumber < 2)last_ecomode++;

    }

}