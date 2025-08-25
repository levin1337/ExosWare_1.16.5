package ru.levinov.modules.impl.combat;

import ru.levinov.events.Event;
import ru.levinov.events.impl.game.EventKey;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.Setting;
import ru.levinov.modules.settings.imp.BindSetting;
import ru.levinov.modules.settings.imp.ModeSetting;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.world.InventoryUtil;

@FunctionAnnotation(
        name = "AutoSwap",
        type = Type.Combat,
        desc = "���� ����� �� �����",
        keywords = {"������","����"}
)
public class AutoSwap extends Function {
    private final TimerUtil timerUtil = new TimerUtil();
    private BindSetting swap = new BindSetting("������ �����", 0);
    private final ModeSetting mode = new ModeSetting("����� ������� ��������", "�����", new String[]{"�����", "���", "������� ������", "���"});
    private final ModeSetting mode1 = new ModeSetting("����� ������� ��������", "�����", new String[]{"�����", "���", "������� ������", "���"});
    boolean swapped = true;
    boolean restart = true;

    public AutoSwap() {
        this.addSettings(new Setting[]{this.swap, this.mode, this.mode1});
    }

    public void onEvent(Event event) {
        if (event instanceof EventKey e) {
            if (e.key == this.swap.getKey() && this.timerUtil.hasTimeElapsed(10L)) {
                if (this.restart) {
                    if (this.mode.is("���")) {
                        this.swap(Items.PLAYER_HEAD);
                    }

                    if (this.mode.is("�����")) {
                        this.swap(Items.TOTEM_OF_UNDYING);
                    }

                    if (this.mode.is("���")) {
                        this.swap(Items.SHIELD);
                    }

                    if (this.mode.is("������� ������")) {
                        this.swap(Items.GOLDEN_APPLE);
                    }

                    this.swapped = true;
                    this.restart = false;
                    this.timerUtil.reset();
                    return;
                }

                if (!this.restart) {
                    if (this.mode1.is("���")) {
                        this.swap(Items.PLAYER_HEAD);
                    }

                    if (this.mode1.is("�����")) {
                        this.swap(Items.TOTEM_OF_UNDYING);
                    }

                    if (this.mode1.is("���")) {
                        this.swap(Items.SHIELD);
                    }

                    if (this.mode1.is("������� ������")) {
                        this.swap(Items.GOLDEN_APPLE);
                    }

                    this.swapped = true;
                    this.restart = true;
                    this.timerUtil.reset();
                }
            }
        }

    }
    public void swap(Item item) {
        if (this.swapped) {
            int slot = InventoryUtil.getItemSlot(item);
            if (slot != -1) {
                mc.playerController.windowClick(0, slot < 9 ? slot + 36 : slot, 40, ClickType.SWAP, mc.player);
                this.swapped = false;
            }
        }

    }
    public void onDisable() {
        this.swapped = true;
        this.restart = true;
        super.onDisable();
    }
}