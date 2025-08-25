package ru.levinov.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;
import ru.levinov.events.Event;
import ru.levinov.events.impl.render.EventRender;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.impl.combat.Aura;
import ru.levinov.modules.settings.Setting;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.util.render.RenderUtil;

import static java.lang.Math.*;

@FunctionAnnotation(
        name = "ChinaHat",
        type = Type.Render,
        desc = " итайска€ шл€па",
        keywords = {"Ўл€па"}
)
public class ChinaHat extends Function {
    public final BooleanOption f5 = new BooleanOption("F5", true);

    public final BooleanOption viewTarget = new BooleanOption("ѕоказывать у всех", true);

    public ChinaHat() {
        this.addSettings(f5, viewTarget);
    }

    public void onEvent(Event event) {

    }
}