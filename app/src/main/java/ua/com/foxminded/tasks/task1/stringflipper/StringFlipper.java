package ua.com.foxminded.tasks.task1.stringflipper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringFlipper {

    private static final Pattern spacePattern = Pattern.compile("[^\\p{Space}]+");
    private Set<Character> toIgnore = new HashSet<>();

    public void setToIgnore(CharSequence toIgnore) {
        if (toIgnore != null) {
            if (this.toIgnore == null) {
                if (toIgnore.length() == 0) return;
                this.toIgnore = new HashSet<>();
            } else {
                clearIgnore();
            }
            if (toIgnore.length() == 0) return;
            addToIgnore(toIgnore);
        }
    }

    public void clearIgnore() {
        toIgnore.clear();
    }

    public boolean hasIgnoreSet() {
        return toIgnore != null && !toIgnore.isEmpty();
    }

    public CharSequence getIgnore() {
        StringBuilder sb = new StringBuilder();
        toIgnore.forEach(sb::append);
        return sb;
    }

    private void addToIgnore(CharSequence toAdd) {
        toAdd.chars()
                .mapToObj(c -> (char) c)
                .filter(c -> !Character.isSpaceChar(c))
                .forEach(this.toIgnore::add);

    }

    public void removeFromIgnore(String toRemove) {
        if (toRemove != null && this.toIgnore != null) {
            toRemove.codePoints()
                    .mapToObj(c -> (char) c)
                    .forEach(this.toIgnore::remove);
        }
    }


    public String rotate(CharSequence toRotate) {
        StringBuffer strBuffer = new StringBuffer();
        Matcher matcher = spacePattern.matcher(toRotate);
        while (matcher.find()) {
            matcher.appendReplacement(strBuffer, rotateWord(matcher.group()));
        }
        matcher.appendTail(strBuffer);
        return strBuffer.toString();
    }

    private String rotateWord(String toRotate) {
//        System.out.println("word = " + toRotate);
        StringBuilder word = new StringBuilder(toRotate);
        int leftIndex = 0;
        int rightIndex = word.length() - 1;

        while (leftIndex < rightIndex) {

            if (this.toIgnore.contains(word.charAt(leftIndex))) {
                leftIndex++;
            } else if (this.toIgnore.contains(word.charAt(rightIndex))) {
                rightIndex--;
            } else {
                char tempLeftChar = word.charAt(leftIndex);
                word.setCharAt(leftIndex, word.charAt(rightIndex));
                word.setCharAt(rightIndex, tempLeftChar);
                leftIndex++;
                rightIndex--;
            }
        }
        return word.toString();
    }
}