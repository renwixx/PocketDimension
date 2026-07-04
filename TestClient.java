import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;

public class TestClient {
    public static void main(String[] args) {
        for (java.lang.reflect.Constructor<?> c : EndPortalBlockEntityRenderer.class.getConstructors()) {
            System.out.println(c);
        }
    }
}
