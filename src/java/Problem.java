import java.util.Vector;

public class Problem {
    private final Vector<Order> orders;
    private float pieceSize;

    public Problem() {
        this.orders = new Vector<Order>();
    }

    public float getPieceSize() {
        return pieceSize;
    }

    public void setPieceSize(float pieceSize) {
        this.pieceSize = pieceSize;
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public Order getOrder(int index) {
        return orders.get(index);
    }

    public int orderCount() {
        return orders.size();
    }

    @Override
    public String toString() {
        return "Problem{" +
                "pieceSize=" + pieceSize +
                ", orders=" + orders +
                '}';
    }
}
