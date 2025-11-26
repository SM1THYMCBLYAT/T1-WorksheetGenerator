package com.espaneg.logic;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class QuickFill {

    // ------------------- ENUM --------------------
    public enum Criteria {
        A_Z,
        a_z,
        NUM_1_20,
        SIGHT_WORDS,
        COLORS,
        ANIMALS,
        CVC_WORDS,
        SHAPES,
        ADDITION,
        SUBTRACTION,
        COUNT_1_10
    }


    // ------------------- DATA LISTS --------------------
    public static final String[] ALPHABET_UPPER = {
            "A","B","C","D","E","F","G","H","I","J","K","L","M",
            "N","O","P","Q","R","S","T","U","V","W","X","Y","Z"
    };

    public static final String[] ALPHABET_LOWER = {
            "a","b","c","d","e","f","g","h","i","j","k","l","m",
            "n","o","p","q","r","s","t","u","v","w","x","y","z"
    };

    public static final int[] NUMBERS_1_20 = {
            1,2,3,4,5,6,7,8,9,10,
            11,12,13,14,15,16,17,18,19,20
    };

    public static final String[] SIGHT_WORDS = {
            "a","and","away","big","blue","can","come","down","find","for",
            "funny","go","help","here","I","in","is","it","jump","little",
            "look","make","me","my","not","one","play","red","run","said",
            "see","the","three","to","two","up","we","where","yellow","you"
    };

    public static final String[] COLORS = {
            "Red","Blue","Yellow","Green","Orange",
            "Purple","Pink","Brown","Black","White","Gray"
    };

    public static final String[] ANIMALS = {
            "Cat","Dog","Cow","Pig","Sheep","Horse",
            "Lion","Tiger","Bear","Elephant","Giraffe",
            "Monkey","Rabbit","Duck","Bird","Fish","Frog"
    };

    public static final String[] CVC_WORDS = {
            "cat","bat","mat","rat","sat",
            "bed","red","pen","men","ten",
            "pin","win","sit","fit","kit",
            "hot","cot","dot","pot","lot",
            "cup","sun","bug","run","fun"
    };

    public static final String[] SHAPES = {
            "Circle","Square","Triangle","Rectangle","Oval",
            "Diamond","Star","Heart"
    };


    // -----------------------------------------------------
    private final JFrame parent; // your main worksheet UI frame

    public QuickFill(JFrame parent) {
        this.parent = parent;
    }


    // ------------------- LOGIC ROUTER --------------------
    public void showCriteria(Criteria c) {
        switch (c) {
            case A_Z -> showAlphabetUppercase();
            case a_z -> showAlphabetLowercase();
            case NUM_1_20 -> showNumbers();
            case SIGHT_WORDS -> showSightWords();
            case COLORS -> showColorList();
            case ANIMALS -> showAnimalList();
            case CVC_WORDS -> showCVCWords();
            case SHAPES -> showShapeSelector();
            case ADDITION -> showAdditionProblems();
            case SUBTRACTION -> showSubtractionProblems();
            case COUNT_1_10 -> showCounting();
        }
    }


    // ---------------- DISPLAY LOGIC ----------------------

    private void showAlphabetUppercase() {
        showListDialog("A - Z Letters", ALPHABET_UPPER);
    }

    private void showAlphabetLowercase() {
        showListDialog("a - z Letters", ALPHABET_LOWER);
    }

    private void showNumbers() {
        String[] numbers = java.util.Arrays.stream(NUMBERS_1_20)
                .mapToObj(String::valueOf).toArray(String[]::new);
        showListDialog("Numbers 1 - 20", numbers);
    }

    private void showSightWords() {
        showListDialog("Sight Words", SIGHT_WORDS);
    }

    private void showColorList() {
        showListDialog("Colors", COLORS);
    }

    private void showAnimalList() {
        showListDialog("Animals", ANIMALS);
    }

    private void showCVCWords() {
        showListDialog("CVC Words", CVC_WORDS);
    }

    private void showAdditionProblems() {
        showListDialog("Addition (auto-generate later)",
                new String[]{"1 + 1", "2 + 3", "4 + 2", "5 + 5"});
    }

    private void showSubtractionProblems() {
        showListDialog("Subtraction (auto-generate later)",
                new String[]{"5 - 2", "7 - 3", "9 - 1"});
    }

    private void showCounting() {
        showListDialog("Counting 1–10",
                new String[]{"One","Two","Three","Four","Five",
                        "Six","Seven","Eight","Nine","Ten"});
    }


    // ---------------- SHAPES (DISPLAY SHAPE IMAGES) ------------------

    public void showShapeSelector() {
        JDialog dlg = new JDialog(parent, "Select Shape", true);
        dlg.setLayout(new GridLayout(0, 2, 10, 10));

        for (String shape : SHAPES) {
            JButton b = new JButton(shape);
            b.addActionListener(e -> showShapeImage(shape));
            dlg.add(b);
        }

        dlg.setSize(300, 300);
        dlg.setLocationRelativeTo(parent);
        dlg.setVisible(true);
    }

    private void showShapeImage(String shape) {
        JDialog dlg = new JDialog(parent, shape, true);
        dlg.setLayout(new BorderLayout());

        JLabel img = new JLabel();
        img.setHorizontalAlignment(SwingConstants.CENTER);

        // Load the shape image from resources
        ImageIcon icon = loadShapeIcon(shape.toLowerCase());
        if (icon != null) {
            img.setIcon(icon);
        } else {
            img.setText("Image not found: " + shape);
            img.setFont(new Font("Arial", Font.BOLD, 18));
        }

        dlg.add(img, BorderLayout.CENTER);

        dlg.setSize(400, 400);
        dlg.setLocationRelativeTo(parent);
        dlg.setVisible(true);
    }

    /**
     * Loads shape image from /resources/shapes folder.
     * Expects lowercase file names, e.g. circle.png
     */
    private ImageIcon loadShapeIcon(String name) {
        try {
            String path = "/shapes/" + name + ".png";
            java.net.URL url = getClass().getResource(path);
            if (url == null) return null;

            ImageIcon raw = new ImageIcon(url);
            Image scaled = raw.getImage().getScaledInstance(300, -1, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    // -------------------- GENERIC LIST DIALOG -----------------------

    private void showListDialog(String title, String[] items) {
        JDialog dlg = new JDialog(parent, title, true);

        JList<String> list = new JList<>(items);
        list.setFont(new Font("Arial", Font.PLAIN, 18));

        dlg.add(new JScrollPane(list));
        dlg.setSize(300, 400);
        dlg.setLocationRelativeTo(parent);
        dlg.setVisible(true);
    }


    // -------------------- SHAPE DRAWING PANEL -----------------------

    private static final Map<String, String> SHAPE_IMAGES = Map.of(
            "Circle", "/images/shapes/circle.png",
            "Square", "/images/shapes/square.png",
            "Triangle", "/images/shapes/triangle.png",
            "Rectangle", "/images/shapes/rectangle.png",
            "Oval", "/images/shapes/oval.png",
            "Diamond", "/images/shapes/diamond.png",
            "Star", "/images/shapes/star.png",
            "Heart", "/images/shapes/heart.png"
    );
    // -------------------- QUICKFILL DATA -----------------------
    public String[] quickfill(Criteria c) {
        return switch (c) {
            case A_Z -> ALPHABET_UPPER;
            case a_z -> ALPHABET_LOWER;
            case NUM_1_20 -> java.util.Arrays.stream(NUMBERS_1_20)
                    .mapToObj(String::valueOf).toArray(String[]::new);
            case SIGHT_WORDS -> SIGHT_WORDS;
            case COLORS -> COLORS;
            case ANIMALS -> ANIMALS;
            case CVC_WORDS -> CVC_WORDS;
            case SHAPES -> SHAPES;
            case ADDITION -> new String[]{"1 + 1", "2 + 3", "4 + 2", "5 + 5"};
            case SUBTRACTION -> new String[]{"5 - 2", "7 - 3", "9 - 1"};
            case COUNT_1_10 -> new String[]{"One","Two","Three","Four","Five",
                    "Six","Seven","Eight","Nine","Ten"};
        };
    }

}
