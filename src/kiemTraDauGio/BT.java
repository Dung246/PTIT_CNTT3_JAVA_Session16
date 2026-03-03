package kiemTraDauGio;
import java.util.*;

interface IRepository<T> {
    boolean add(T item);
    boolean removeById(String id);
    T findById(String id);
    List<T> findAll();
}

abstract class Product {
    protected String id;
    protected String name;
    protected double price;

    public Product(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
    public String getId() {
        return id;
    }
    public double getPrice() {
        return price;
    }
    public abstract double calculateFinalPrice();
    public void displayInfo() {
        System.out.println("Mã:" + id + "| Tên: " + name + "| Giá gốc: " + price);
    }
}

class ElectronicProduct extends Product {
    private int warrantyMonths;
    public ElectronicProduct(String id,String name,double price,int warrantyMonths) {
        super(id, name, price);
        this.warrantyMonths = warrantyMonths;
    }
    @Override
    public double calculateFinalPrice() {
        if (warrantyMonths > 12) {
            return price + 1_000_000;
        }
        return price;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Số tháng bảo hành: " + warrantyMonths + " tháng");
    }
}
class FoodProduct extends Product {
    private int discountPercent;
    public FoodProduct(String id, String name, double price, int discountPercent) {
        super(id, name, price);
        this.discountPercent = discountPercent;
    }
    @Override
    public double calculateFinalPrice() {
        return price - (price * discountPercent/100.0);
    }
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Giảm giá: " + discountPercent + "%");
    }
}

class ProductRepository implements IRepository<Product> {

    private List<Product> productList = new ArrayList<>();
    private Map<String, Product> productMap = new HashMap<>();

    @Override
    public boolean add(Product item) {
        if (item == null || item.getId() == null) {
            return false;
        }

        if (productMap.containsKey(item.getId())) {
            return false;
        }

        productList.add(item);
        productMap.put(item.getId(), item);
        return true;
    }

    @Override
    public boolean removeById(String id) {
        if (id == null || !productMap.containsKey(id)) {
            return false;
        }

        Product product = productMap.remove(id);
        productList.remove(product);
        return true;
    }

    @Override
    public Product findById(String id) {
        if (id == null) return null;
        return productMap.get(id);
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(productList);
    }

    public Map<String, Integer> countByType() {
        Map<String, Integer> result = new HashMap<>();

        for (Product p : productList) {
            if (p instanceof ElectronicProduct) {
                result.put("Electronic",
                        result.getOrDefault("Electronic", 0) + 1);
            } else if (p instanceof FoodProduct) {
                result.put("Food",
                        result.getOrDefault("Food", 0) + 1);
            }
        }

        return result;
    }
}

public class BT {
    public static void main(String[] args) {
        ProductRepository repo = new ProductRepository();
        repo.add(new ElectronicProduct("ID01", "Dây sạc", 20000000, 24));
        repo.add(new ElectronicProduct("ID02", "Tai nghe", 2000000, 6));
        repo.add(new FoodProduct("A01", "Bánh mì", 20000, 9));
        repo.add(new FoodProduct("A02", "Sữa", 30000, 5));
        System.out.println("===== DANH SÁCH SẢN PHẨM =====");
        List<Product> products = repo.findAll();
        if (products != null) {
            for (Product p : products) {
                if (p != null) {
                    p.displayInfo();
                    System.out.println("Thành tiền: " + p.calculateFinalPrice());
                }
            }
        }
        System.out.println("\nTÌM SẢN PHẨM ");

        Product found = repo.findById("ID01");
        if (found != null) {
            found.displayInfo();
            System.out.println("Thành tiền: " + found.calculateFinalPrice());
        } else {
            System.out.println("Không tìm thấy sản phẩm!");
        }

        System.out.println("\nSẮP XẾP THEO GIÁ TĂNG DẦN");
        Collections.sort(products, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return Double.compare(o1.getPrice(), o2.getPrice());
            }
        });

        for (Product p : products) {
            System.out.println(p.getId() + " - " + p.getPrice());
        }

        System.out.println("\nTHỐNG KÊ THEO LOẠI");
        Map<String, Integer> stats = repo.countByType();
        }
    }
