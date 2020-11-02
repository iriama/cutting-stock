public class Order {

    private final float size;
    private final int count;

    public Order(float size, int count) {
        this.size = size;
        this.count = count;
    }

    public float getSize() {
        return size;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "Order{" +
                "size=" + size +
                ", count=" + count +
                '}';
    }
}
