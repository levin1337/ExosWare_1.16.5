package ru.levinov.modules.impl.Misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CChatMessagePacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import ru.levinov.events.Event;
import ru.levinov.events.impl.game.EventKey;
import ru.levinov.events.impl.packet.EventPacket;
import ru.levinov.managment.Managment;
import ru.levinov.modules.Function;
import ru.levinov.modules.FunctionAnnotation;
import ru.levinov.modules.Type;
import ru.levinov.modules.settings.imp.BindSetting;
import ru.levinov.modules.settings.imp.BooleanOption;
import ru.levinov.util.ClientUtil;
import ru.levinov.util.misc.TimerUtil;
import ru.levinov.util.world.InventoryUtil;

import java.util.Arrays;

@FunctionAnnotation(
        name = "GriefHelper",
        type = Type.Misc,
        desc = "Помощь в пвп или хвх"
)
public class GriefHelper extends Function {
    private final BindSetting disorient = new BindSetting("Дезориентация", 0);
    private final BindSetting trap = new BindSetting("Трапка", 0);
    private final BindSetting otm = new BindSetting("Отмычка", 0);
    private final BindSetting pero = new BindSetting("Пёрышко", 0);
    private final BindSetting livalka = new BindSetting("Ливалка", 0);
    private final BindSetting chest = new BindSetting("Переносной /enderchest", 0);
    private BindSetting dropCords = new BindSetting("Координаты", 0);

    private BindSetting fireball = new BindSetting("ФаерБолл", 0);



    public final BooleanOption specc = new BooleanOption("Уведовление о спеке", true);
    public final BooleanOption warps = new BooleanOption("Уведовление о варпе", false);
    public final BooleanOption messageblacklist = new BooleanOption("Блокировка слов", true);
    public final BooleanOption staff = new BooleanOption("Предупреждение о стаффе", true);
    public final BooleanOption eqeglazz = new BooleanOption("Авто глаз короля пауков", true);

    private final TimerUtil disorientTimer = new TimerUtil();
    private final TimerUtil trapTimer = new TimerUtil();
    private final String[] spectext = new String[]{"Spec", "Спек", "spec", "спек", "SPEC", "СПЕК", "Спек", "spek"};
    private final String[] warptext = new String[]{"Warp", "Варп", "warp", "варп", "WARP", "ВАРП", "Варп"};

    String[] banWords = new String[]{"экспа", "экспенсив", "экспой",
            "нуриком", "целкой", "нурлан", "нурсултан", "целестиал",
            "целка", "нурик", "атернос", "expa", "celka",
            "nurik", "expensive", "celestial", "nursultan",
            "фанпей", "funpay", "fluger",
            "акриен", "akrien", "фантайм",
            "ft", "funtime", "безмамный", "rich", "рич",
            "без мамный", "wild", "вилд", "excellent",
            "экселлент", "hvh", "хвх", "matix", "impact",
            "матикс", "импакт", "wurst","трапоних"};

    public GriefHelper() {
        addSettings(disorient,trap,otm,pero,livalka ,chest,dropCords,fireball ,specc,warps,messageblacklist,staff,eqeglazz);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventKey eventKey) {
            if (eventKey.key == this.disorient.getKey() && this.disorientTimer.hasTimeElapsed(3000L) && mc.currentScreen == null) {
                this.use(this.getDisorientAtHotBar(), this.getDisorientAtInventory());
                this.disorientTimer.reset();
            }

            if (eventKey.key == this.trap.getKey() && this.trapTimer.hasTimeElapsed(3000L) && mc.currentScreen == null) {
                this.use(this.getTrapAtHotBar(), this.getTrapAtInventory());
                this.trapTimer.reset();
            }
            if (eventKey.key == this.otm.getKey() && mc.currentScreen == null) {
                this.use(this.getOtmAtHotBar(), this.getOtmAtInventory());
            }

                if (eventKey.key == this.pero.getKey() && mc.currentScreen == null) {
                    this.use(this.getperoAtHotBar(), this.getPeroAtInventory());
                }

            if (eventKey.key == this.livalka.getKey() && mc.currentScreen == null) {
                this.use(this.getLivalkaAtHotBar(), this.getLivalkaAtInventory());
            }

            if (eventKey.key == this.chest.getKey() && mc.currentScreen == null) {
                this.use(this.getchestAtHotBar(), this.getchestAtInventory());
            }
            if (eventKey.key == fireball.getKey() && mc.currentScreen == null) {
                useLegit(getFireBallAtHotBar(), getFireBallAtInventory());
            }
            if (eventKey.key == this.dropCords.getKey() && mc.currentScreen == null) {
                mc.player.sendChatMessage("! Мои координаты x" + (int)mc.player.getPosX() + " y" + (int)mc.player.getPosY()  + " z" + (int)mc.player.getPosZ());
            }
            if (eqeglazz.get()) {
                if (mc.player.getHealth() < 4.0f) {
                    use(this.getGlazzAtHotBar(), this.getGlazzAtInventory());
                }
            }
        }

        //Спек
        if (specc.get()) {
            if (event instanceof EventPacket packetEvent) {
                if (packetEvent.isReceivePacket()) {
                    if (packetEvent.getPacket() instanceof SChatPacket packetChat) {
                        spec(packetChat);
                    }
                }
            }
        }
        if (warps.get()) {
            if (event instanceof EventPacket packetEvent) {
                if (packetEvent.isReceivePacket()) {
                    if (packetEvent.getPacket() instanceof SChatPacket packetChat) {
                        warp(packetChat);
                    }
                }
            }
        }
        if (messageblacklist.get()) {
            if (event instanceof EventPacket eventPacket) {
                if (((EventPacket) eventPacket).isSendPacket()) {
                    if (((EventPacket) event).getPacket() instanceof CChatMessagePacket p) {
                        boolean contains = false;
                        for (String str : banWords) {
                            if (!p.getMessage().toLowerCase().contains(str)) continue;
                            contains = true;
                            break;

                        }
                        if (contains) {
                            ClientUtil.sendMesage(TextFormatting.RED + " Обнаружены запрещенные слова в вашем сообщении. " + "Отправка отменена, чтобы избежать бана на ReallyWorld.");
                            event.setCancel(true);
                        }
                    }
                }
            }
        }
    }
    private void spec(SChatPacket packet) {
        String message = TextFormatting.getTextWithoutFormattingCodes(packet.getChatComponent().getString());

        if (БИМБАМБУМ(message)) {
            ПАЙТОНСУКА();

        }
    }
    private boolean БИМБАМБУМ(String message) {
        return Arrays.stream(this.spectext)
                .map(String::toLowerCase)
                .anyMatch(message::contains);
    }
    private void ПАЙТОНСУКА() {
        ClientUtil.sendMesage(TextFormatting.RED + "Игрок из чата просит проследить за ним!");
        mc.world.playSound(mc.player, mc.player.getPosition(), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 1.0F, 1.0F);
        Managment.NOTIFICATION_MANAGER.add(TextFormatting.GOLD + "Игрок просит проследить за ним!" , "Spec", 4);
    }




    private void warp(SChatPacket packet) {
        String message = TextFormatting.getTextWithoutFormattingCodes(packet.getChatComponent().getString());

        if (БАХ(message)) {
            ПАХ();

        }
    }
    private boolean БАХ(String message) {
        return Arrays.stream(this.warptext)
                .map(String::toLowerCase)
                .anyMatch(message::contains);
    }
    private void ПАХ() {
        ClientUtil.sendMesage(TextFormatting.RED + "Игрок зазывает на варп!");
        Managment.NOTIFICATION_MANAGER.add(TextFormatting.GOLD + "Игрок зазывает на варп!" , "Spec", 4);
    }



    private void use(int n, int n2) {
        if (n != -1) {
            int n3 = mc.player.inventory.currentItem;
            mc.player.inventory.currentItem = n;
            mc.player.connection.sendPacket(new CHeldItemChangePacket(n));
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            mc.player.inventory.currentItem = n3;
            mc.player.connection.sendPacket(new CHeldItemChangePacket(n3));

        } else if (n2 != -1) {
            mc.playerController.windowClick(0, n2, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            mc.playerController.windowClick(0, n2, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
        }

        disorientTimer.reset();
    }
    private void useLegit(int n, int n2) {
        if (n != -1) {
            mc.player.connection.sendPacket(new CHeldItemChangePacket(InventoryUtil.getItem(Items.FIRE_CHARGE, true)));
         //   mc.player.inventory.currentItem = InventoryUtil.getItem(Items.FIRE_CHARGE, true);
        //    mc.player.swingArm(Hand.MAIN_HAND);
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            int previousSlot = InventoryUtil.getItem(Items.DIAMOND_SWORD, true);

            if (previousSlot == -1) {
                previousSlot = InventoryUtil.getItem(Items.NETHERITE_SWORD, true);
            }
            if (previousSlot == -1) {
                previousSlot = InventoryUtil.getItem(Items.IRON_SWORD, true);
            }
            if (previousSlot == -1) {
                previousSlot = InventoryUtil.getItem(Items.STONE_SWORD, true);
            }
            if (previousSlot == -1) {
                previousSlot = InventoryUtil.getItem(Items.WOODEN_SWORD, true);
            }
            mc.player.inventory.currentItem = previousSlot;
            mc.player.connection.sendPacket(new CHeldItemChangePacket(previousSlot));

        }
    }

    private int getDisorientAtHotBar() {
        for(int i = 0; i < 9; ++i) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == Items.ENDER_EYE) {
                return i;
            }
        }

        return -1;
    }

    private int getTrapAtHotBar() {
        for(int i = 0; i < 9; ++i) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == Items.NETHERITE_SCRAP) {
                return i;
            }
        }

        return -1;
    }

    private int getDisorientAtInventory() {
        for(int i = 36; i >= 0; --i) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == Items.ENDER_EYE) {
                if (i < 9) {
                    i += 36;
                }

                return i;
            }
        }

        return -1;
    }

    private int getTrapAtInventory() {
        for(int i = 36; i >= 0; --i) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == Items.NETHERITE_SCRAP) {
                if (i < 9) {
                    i += 36;
                }

                return i;
            }
        }

        return -1;
    }
    private int getOtmAtHotBar() {
        for(int i = 0; i < 9; ++i) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == Items.TRIPWIRE_HOOK) {
                return i;
            }
        }

        return -1;
    }

    private int getLivalkaAtHotBar() {
        for(int i = 0; i < 9; ++i) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == Items.POPPED_CHORUS_FRUIT) {
                return i;
            }
        }

        return -1;
    }
    private int getGlazzAtHotBar() {
        for(int i = 0; i < 9; ++i) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == Items.FERMENTED_SPIDER_EYE) {
                return i;
            }
        }

        return -1;
    }
    private int getGlazzAtInventory() {
        for(int i = 36; i >= 0; --i) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == Items.FERMENTED_SPIDER_EYE) {
                if (i < 9) {
                    i += 36;
                }

                return i;
            }
        }

        return -1;
    }

    private int getchestAtHotBar() {
        for(int i = 0; i < 9; ++i) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == Items.PLAYER_HEAD) {
                return i;
            }
        }

        return -1;
    }

//
     ///
     /// РАЗМУТИТЕ ПЖ Я БОЛЬШЕ НЕ БУДУ
     ///      /// РАЗМУТИТЕ ПЖ Я БОЛЬШЕ НЕ БУДУ
     ///      /// РАЗМУТИТЕ ПЖ Я БОЛЬШЕ НЕ БУДУ
     ///      /// РАЗМУТИТЕ ПЖ Я БОЛЬШЕ НЕ БУДУ
     ///      /// РАЗМУТИТЕ ПЖ Я БОЛЬШЕ НЕ БУДУ
     ///      /// РАЗМУТИТЕ ПЖ Я БОЛЬШЕ НЕ БУДУ
     ///      /// РАЗМУТИТЕ ПЖ Я БОЛЬШЕ НЕ БУДУ
     ///      /// РАЗМУТИТЕ ПЖ Я БОЛЬШЕ НЕ БУДУ
     ///      /// РАЗМУТИТЕ ПЖ Я БОЛЬШЕ НЕ БУДУ
     ///      /// РАЗМУТИТЕ ПЖ Я БОЛЬШЕ НЕ БУДУ
     ///      /// РАЗМУТИТЕ ПЖ Я БОЛЬШЕ НЕ БУДУ
     ///      /// РАЗМУТИТЕ ПЖ Я БОЛЬШЕ НЕ БУДУ
     ///      /// РАЗМУТИТЕ ПЖ Я БОЛЬШЕ НЕ БУДУ
     ///
     ///
    private int getFireBallAtHotBar() {
        for(int i = 0; i < 9; ++i) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == Items.FIRE_CHARGE) {
                return i;
            }
        }

        return -1;
    }

    private int getFireBallAtInventory() {
        for(int i = 36; i >= 0; --i) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == Items.FIRE_CHARGE) {
                if (i < 9) {
                    i += 36;
                }

                return i;
            }
        }

        return -1;
    }



    private int getperoAtHotBar() {
        for(int i = 0; i < 9; ++i) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == Items.FEATHER) {
                return i;
            }
        }

        return -1;
    }

    private int getOtmAtInventory() {
        for(int i = 36; i >= 0; --i) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == Items.TRIPWIRE_HOOK) {
                if (i < 9) {
                    i += 36;
                }

                return i;
            }
        }

        return -1;
    }

    private int getLivalkaAtInventory() {
        for(int i = 36; i >= 0; --i) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == Items.POPPED_CHORUS_FRUIT) {
                if (i < 9) {
                    i += 36;
                }

                return i;
            }
        }

        return -1;
    }

    private int getchestAtInventory() {
        for(int i = 36; i >= 0; --i) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == Items.PLAYER_HEAD) {
                if (i < 9) {
                    i += 36;
                }

                return i;
            }
        }

        return -1;
    }

    private int getPeroAtInventory() {
        for(int i = 36; i >= 0; --i) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == Items.FEATHER) {
                if (i < 9) {
                    i += 36;
                }

                return i;
            }
        }

        return -1;
    }
}