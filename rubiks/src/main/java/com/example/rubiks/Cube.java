package com.example.rubiks;

//                         UP (White: 0)
//                         [ 0][ 1][ 2]
//                         [ 3][ 4][ 5]
//                         [ 6][ 7][ 8]
//
//        LEFT (Orange: 1) FRONT (Green: 2) RIGHT (Red: 3)   BACK (Blue: 4)
//        [ 9][10][11]     [18][19][20]     [27][28][29]     [36][37][38]
//        [12][13][14]     [21][22][23]     [30][31][32]     [39][40][41]
//        [15][16][17]     [24][25][26]     [33][34][35]     [42][43][44]
//
//                         DOWN (Yellow: 5)
//                         [45][46][47]
//                         [48][49][50]
//                         [51][52][53]

import java.util.Arrays;

public class Cube {

    private static final byte[] solvedState = {
            // up (white)
            0,0,0,0,0,0,0,0,0,
            // left (orange)
            1,1,1,1,1,1,1,1,1,
            // front (green)
            2,2,2,2,2,2,2,2,2,
            // right (red)
            3,3,3,3,3,3,3,3,3,
            // back (blue)
            4,4,4,4,4,4,4,4,4,
            // down (yellow)
            5,5,5,5,5,5,5,5,5,
    };

    byte[] cube = {
            // up (white)
            0,0,0,0,0,0,0,0,0,
            // left (orange)
            1,1,1,1,1,1,1,1,1,
            // front (green)
            2,2,2,2,2,2,2,2,2,
            // right (red)
            3,3,3,3,3,3,3,3,3,
            // back (blue)
            4,4,4,4,4,4,4,4,4,
            // down (yellow)
            5,5,5,5,5,5,5,5,5,
    };

    public boolean isSolved() {
        return Arrays.equals(this.cube, solvedState);
    }

    public byte[][] getCorners() {

        // return colours of each corner
        return new byte[][] {

                {cube[0], cube[9], cube[38]},
                {cube[2], cube[36], cube[29]},
                {cube[6], cube[18], cube[11]},
                {cube[8], cube[27], cube[20]},
                {cube[51], cube[44], cube[15]},
                {cube[53], cube[35], cube[42]},
                {cube[45], cube[17], cube[24]},
                {cube[47], cube[26], cube[33]},
        };
    }

    public void setState(byte[] newState) {

        this.cube = newState.clone();
    }

    public String showCube() {

        return Arrays.toString(cube);
    }

    public void executeMove(int move) {

        switch (move) {
            case 0: turnF(); break;
            case 1: FPrime(); break;
            case 2: turnB(); break;
            case 3: BPrime(); break;
            case 4: turnL(); break;
            case 5: LPrime(); break;
            case 6: turnR(); break;
            case 7: RPrime(); break;
            case 8: turnU(); break;
            case 9: UPrime(); break;
            case 10: turnD(); break;
            case 11: DPrime(); break;
            case 12: turnF2(); break;
            case 13: turnB2(); break;
            case 14: turnL2(); break;
            case 15: turnR2(); break;
            case 16: turnU2(); break;
            case 17: turnD2(); break;
            default: throw new IllegalArgumentException("Invalid move: "+move);
        }
    }

    public Cube clone() {

        Cube newCube = new Cube();

        // copy the 54 bytes from this cube into the new cube object
        // sourceArray, sourceStartIndex, destinationArray, destinationStartIndex, length
        System.arraycopy(this.cube, 0, newCube.cube, 0, 54);
        return newCube;
    }

    // rotates 4 values in array (clockwise)
    public void rotate(int a, int b,int c,int d) {

        byte temp = cube[d];

        cube[d] = cube[c];
        cube[c] = cube[b];
        cube[b] = cube[a];
        cube[a] = temp;
    }


    // turn moves

    public void turnF() {

        // rotate edges of front face (green)
        rotate(19,23,25,21);

        // rotate corners of front face (green)
        rotate(18,20,26,24);

        // rotate adjacent edges
        rotate(7,30,46,14);

        // rotate adjacent corners
        rotate(6,27,47,17);

        rotate(8,33,45,11);
    }

    // reverse order of parameters
    public void FPrime() {

        // rotate edges of front face (green)
        rotate(21,25,23,19);

        // rotate corners of front face (green)
        rotate(24,26,20,18);

        // rotate adjacent edges
        rotate(14,46,30,7);

        // rotate adjacent corners
        rotate(17,47,27,6);

        rotate(11,45,33,8);
    }

    public void turnB() {

        rotate(37,41,43,39);
        rotate(36,38,44,42);

        rotate(1,12,52,32);
        rotate(2,9,51,35);
        rotate(0,15,53,29);
    }

    public void BPrime() {

        rotate(39,43,41,37);
        rotate(42,44,38,36);

        rotate(32,52,12,1);
        rotate(35,51,9,2);
        rotate(29,53,15,0);
    }

    public void turnL() {

        rotate(10,14,16,12);
        rotate(9,11,17,15);

        rotate(3,21,48,41);
        rotate(0,18,45,44);
        rotate(6,24,51,38);
    }

    public void LPrime() {

        rotate(12,16,14,10);
        rotate(15,17,11,9);

        rotate(41,48,21,3);
        rotate(44,45,18,0);
        rotate(38,51,24,6);
    }

    public void turnR() {

        rotate(28,32,34,30);
        rotate(27,29,35,33);

        rotate(5,39,50,23);
        rotate(8,36,53,26);
        rotate(2, 42,47,20);
    }

    public void RPrime() {

        rotate(30,34,32,28);
        rotate(33,35,29,27);

        rotate(23,50,39,5);
        rotate(26,53,36,8);
        rotate(20,47,42,2);
    }

    public void turnU() {

        rotate(1,5,7,3);
        rotate(0,2,8,6);

        rotate(37,28,19,10);
        rotate(38, 29, 20, 11);
        rotate(36, 27,18,9);
    }

    public void UPrime() {

        rotate(3,7,5,1);
        rotate(6,8,2,0);

        rotate(10,19,28,37);
        rotate(11,20,29,38);
        rotate(9,18,27,36);
    }

    public void turnD() {

        rotate(46,50,52,48);
        rotate(45,47,53,51);

        rotate(25,34,43,16);
        rotate(24,33,42,15);
        rotate(26,35,44,17);
    }

    public void DPrime() {

        rotate(48,52,50,46);
        rotate(51,53,47,45);

        rotate(16,43,34,25);
        rotate(15,42,33,24);
        rotate(17,44,35,26);
    }

    // 180 turns
    public void turnF2() {

        turnF();
        turnF();
    }

    public void turnB2() {

        turnB();
        turnB();
    }

    public void turnL2() {

        turnL();
        turnL();
    }

    public void turnR2() {

        turnR();
        turnR();
    }

    public void turnU2() {

        turnU();
        turnU();
    }

    public void turnD2() {

        turnD();
        turnD();
    }
}