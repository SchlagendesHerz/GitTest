package ua.com.foxminded.tasks.task1.stringflipper;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringFlipper {

    private static final Pattern SPACE_PATTERN = Pattern.compile("[^\\p{Space}]+");
    private Set<Character> toIgnore;

    public void setToIgnore(CharSequence toIgnore) {
        if (toIgnore != null) {
            if (this.toIgnore == null) {
                if (toIgnore.length() == 0) return;
                this.toIgnore = new LinkedHashSet<>();
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
        if (toIgnore != null) {
            toIgnore.forEach(sb::append);
        }
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


    public CharSequence rotate(CharSequence toRotate) {
        StringBuffer strBuffer = new StringBuffer();
        Matcher matcher = SPACE_PATTERN.matcher(toRotate);
        while (matcher.find()) {
            matcher.appendReplacement(strBuffer, rotateWord(matcher.group()));
        }
        matcher.appendTail(strBuffer);
        return strBuffer;
    }

    private boolean isInIgnore(CharSequence toCheck) {
        if (toIgnore == null || toIgnore.isEmpty()) {
            return false;
        }
        for (int i = 0; i < toCheck.length(); i++) {
            if (toIgnore.contains(toCheck.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private String rotateWord(CharSequence toRotate) {
        StringBuilder word = new StringBuilder(toRotate);

        if (isInIgnore(toRotate)) {
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
        } else {
            word = word.reverse();
        }
        return word.toString();
    }

}