import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.block.entity.EndPortalBlockEntity;

public class Test {
    public static void main(String[] args) {
        System.out.println(EndPortalBlockEntityRenderer.class.getTypeParameters()[0].getBounds()[0].getTypeName());
    }
}
