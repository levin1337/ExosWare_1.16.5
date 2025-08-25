package net.minecraft.client.renderer.entity.layers;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.UUID;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.SkullTileEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;
import ru.levinov.managment.Managment;
import ru.levinov.managment.friend.FriendManager;
import ru.levinov.util.render.BloomHelper;
import ru.levinov.util.render.RenderUtil;
import ru.levinov.util.render.animation.AnimationMath;

import static com.mojang.blaze3d.systems.RenderSystem.*;
import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_COLOR;
import static net.optifine.util.MathUtils.PI2;
import static org.lwjgl.opengl.GL11.GL_FLAT;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11C.*;

public class HeadLayer<T extends LivingEntity, M extends EntityModel<T> & IHasHead> extends LayerRenderer<T, M>
{
    private final float field_239402_a_;
    private final float field_239403_b_;
    private final float field_239404_c_;

    public HeadLayer(IEntityRenderer<T, M> p_i50946_1_)
    {
        this(p_i50946_1_, 1.0F, 1.0F, 1.0F);
    }

    public HeadLayer(IEntityRenderer<T, M> p_i232475_1_, float p_i232475_2_, float p_i232475_3_, float p_i232475_4_)
    {
        super(p_i232475_1_);
        this.field_239402_a_ = p_i232475_2_;
        this.field_239403_b_ = p_i232475_3_;
        this.field_239404_c_ = p_i232475_4_;
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EquipmentSlotType.HEAD);

        if (!itemstack.isEmpty())
        {
            Item item = itemstack.getItem();
            matrixStackIn.push();
            matrixStackIn.scale(this.field_239402_a_, this.field_239403_b_, this.field_239404_c_);
            boolean flag = entitylivingbaseIn instanceof VillagerEntity || entitylivingbaseIn instanceof ZombieVillagerEntity;

            if (entitylivingbaseIn.isChild() && !(entitylivingbaseIn instanceof VillagerEntity))
            {
                float f = 2.0F;
                float f1 = 1.4F;
                matrixStackIn.translate(0.0D, 0.03125D, 0.0D);
                matrixStackIn.scale(0.7F, 0.7F, 0.7F);
                matrixStackIn.translate(0.0D, 1.0D, 0.0D);
            }

            this.getEntityModel().getModelHead().translateRotate(matrixStackIn);

            if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock)
            {
                float f3 = 1.1875F;
                matrixStackIn.scale(1.1875F, -1.1875F, -1.1875F);

                if (flag)
                {
                    matrixStackIn.translate(0.0D, 0.0625D, 0.0D);
                }

                GameProfile gameprofile = null;

                if (itemstack.hasTag())
                {
                    CompoundNBT compoundnbt = itemstack.getTag();

                    if (compoundnbt.contains("SkullOwner", 10))
                    {
                        gameprofile = NBTUtil.readGameProfile(compoundnbt.getCompound("SkullOwner"));
                    }
                    else if (compoundnbt.contains("SkullOwner", 8))
                    {
                        String s = compoundnbt.getString("SkullOwner");

                        if (!StringUtils.isBlank(s))
                        {
                            gameprofile = SkullTileEntity.updateGameProfile(new GameProfile((UUID)null, s));
                            compoundnbt.put("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), gameprofile));
                        }
                    }
                }

                matrixStackIn.translate(-0.5D, 0.0D, -0.5D);
                SkullTileEntityRenderer.render((Direction)null, 180.0F, ((AbstractSkullBlock)((BlockItem)item).getBlock()).getSkullType(), gameprofile, limbSwing, matrixStackIn, bufferIn, packedLightIn);
            }
            else if (!(item instanceof ArmorItem) || ((ArmorItem)item).getEquipmentSlot() != EquipmentSlotType.HEAD)
            {
                float f2 = 0.625F;
                matrixStackIn.translate(0.0D, -0.25D, 0.0D);
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
                matrixStackIn.scale(0.625F, -0.625F, -0.625F);

                if (flag)
                {
                    matrixStackIn.translate(0.0D, 0.1875D, 0.0D);
                }

                Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.HEAD, false, matrixStackIn, bufferIn, packedLightIn);
            }

            matrixStackIn.pop();
        }
        if (Managment.FUNCTION_MANAGER.chinaHat.state && entitylivingbaseIn instanceof PlayerEntity player && ((player instanceof ClientPlayerEntity) || FriendManager.isFriend(TextFormatting.getTextWithoutFormattingCodes(player.getName().getString())))) {
            float width = player.getWidth();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            enableDepthTest();
            disableTexture();
            enableBlend();
            defaultBlendFunc();
            disableCull();
            shadeModel(GL_SMOOTH);
            GL11.glEnable(GL_LINE_SMOOTH);
            lineWidth(3);
            matrixStackIn.push();
            float[] colors = null;
            {

                float offset = player.inventory.armorInventory.get(3).isEmpty() ? -.41f : -.5f;
                getEntityModel().getModelHead().translateRotate(matrixStackIn);
                matrixStackIn.translate(0, offset, 0);
                matrixStackIn.rotate(Vector3f.ZN.rotationDegrees(180f));
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90));
                buffer.begin(GL_TRIANGLE_FAN, POSITION_COLOR);
                {
                    colors = RenderUtil.IntColor.rgb(Managment.STYLE_MANAGER.getCurrentStyle().getColor(1));
                    buffer.pos(matrixStackIn.getLast().getMatrix(), 0, .4f, 0).color(colors[0], colors[1], colors[2], 160F).endVertex();
                    for (int i = 0, size = 360; i <= size; i++) {
                        colors = RenderUtil.IntColor.rgb(Managment.STYLE_MANAGER.getCurrentStyle().getColor(i));
                        buffer.pos(matrixStackIn.getLast().getMatrix(), -MathHelper.sin(i * PI2 / size) * width, 0, MathHelper.cos(i * PI2 / size) * width).color(colors[0], colors[1], colors[2], 1F).endVertex();
                    }
                }

                tessellator.draw();
                buffer.begin(GL_LINE_LOOP, POSITION_COLOR);
                {
                    for (int i = 0, size = 360; i <= size; i++) {
                        colors = RenderUtil.IntColor.rgb(Managment.STYLE_MANAGER.getCurrentStyle().getColor(i));
                        buffer.pos(matrixStackIn.getLast().getMatrix(),
                                        -MathHelper.sin(i * PI2 / size) * width, 0,
                                        MathHelper.cos(i * PI2 / size) * width).color(colors[0], colors[1], colors[2], 1F).endVertex();
                    }
                }
                depthMask(false);
                tessellator.draw();
                depthMask(true);

            }
            matrixStackIn.pop();
            disableDepthTest();
            disableBlend();
            enableTexture();
            shadeModel(GL_FLAT);
            enableCull();
        }
    }
}
