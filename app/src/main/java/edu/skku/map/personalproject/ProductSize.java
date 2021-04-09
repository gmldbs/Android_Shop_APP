package edu.skku.map.personalproject;

public final class ProductSize {

    //region GLOBALS
    private int size;
    private int sizeQuantity;

    //endregion

    public ProductSize(int size, int sizeQuantity) {
        this.size = size;
        this.sizeQuantity = sizeQuantity;
    }

    public int getSize() {
        return size;
    }


    public int getSizeQuantity() {
        return sizeQuantity;
    }

    public void setSizeQuantity(int sizeQuantity) {
        this.sizeQuantity = sizeQuantity;
    }
}
