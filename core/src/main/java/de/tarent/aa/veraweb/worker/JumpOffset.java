package de.tarent.aa.veraweb.worker;
public class JumpOffset {

    final private String letter;
    final private int offset;
    final private int targetPageOffset;
    final private int currentPageOffset;

    public JumpOffset(String letter, Integer offset, Integer currentPageOffset, Integer itemsPerPage) {
        this.letter = letter;
        this.offset = offset;
        this.currentPageOffset = currentPageOffset;
        this.targetPageOffset = (offset / itemsPerPage) * itemsPerPage;
    }

    public String getLetter() {
        return letter;
    }

    public Integer getOffset() {
        return offset;
    }

    public Integer getTargetPageOffset() {
        return targetPageOffset;
    }

    public boolean isTargetOnCurrentPage() {
        return targetPageOffset == currentPageOffset;
    }
}
