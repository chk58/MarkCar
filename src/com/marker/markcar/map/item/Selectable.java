package com.marker.markcar.map.item;

public interface Selectable {
    public boolean isSelected();
    public void setSelected(boolean selected);
    public boolean contains(float x, float y);
}
