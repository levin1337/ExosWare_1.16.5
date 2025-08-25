package ru.levinov.modules;

import ru.levinov.modules.impl.Misc.*;
import ru.levinov.modules.impl.player.InventoryMoveFunction;
import ru.levinov.modules.impl.player.NoPushFunction;
import ru.levinov.modules.impl.render.*;
import ru.levinov.modules.impl.combat.*;
import ru.levinov.modules.impl.movement.*;
import ru.levinov.modules.impl.player.*;
import ru.levinov.modules.impl.player.*;
import ru.levinov.modules.impl.util.*;
import ru.levinov.modules.impl.util.NoCommands;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FunctionManager {

    public static final List<Function> functions = new CopyOnWriteArrayList<>();
    public final ArrowsFunction arrowsFunction;
    public final FullBrightFunction fullBrightFunction;
    public final SprintFunction sprintFunction;
    public final FlightFunction flightFunction;
    public final StrafeFunction strafeFunction;
    public final ElytraBoost elytraBoost;
    public final Item360 item360;
    public final TimerFunction timerFunction;
    public final ItemPhysic itemPhysic;
    public final BabyBoy babyBoy;
    public final CameraSpec cameraSpec;
    public final AutoPotionFunction autoPotionFunction;
    public final AutoRespawnFunction autoRespawnFunction;
    public final VelocityFunction velocityFunction;
    public final MiddleClickPearlFunction middleClickPearlFunction;
    public final AutoTotemFunction autoTotemFunction;
    public final InventoryMoveFunction inventoryMoveFunction;
    public final NoPushFunction noPushFunction;
    public final Xbox xbox;

    public final NoSlowFunction noSlowFunction;
    public final Aura auraFunction;
    public final ButtonBloom buttonBloom;
    public final NoServerRotFunction noServerRotFunction;
    public final FastBreakFunction fastBreakFunction;
    public final SwingAnimationFunction swingAnimationFunction;
    public final AutoGAppleFunction autoGApple;
    public final Particles particleses;
    public final NoRenderFunction noRenderFunction;
    public final GappleCooldownFunction gappleCooldownFunction;
    public final Optimization optimization;
    public final ItemScroller itemScroller;
    public final NameTags nameTags;
    public final NoInteractFunction noInteractFunction;
    public final CustomWorld customWorld;
    public final ClientSounds clientSounds;
    public final Crosshair crosshair;
    public final StreamMod streamMod;
    public final NameProtect nameProtect;
    public final NoCommands noCommands;
    public final UnHookFunction unhook;
    public final ChinaHat chinaHat;


    public final ExtendedTab extendedTab;


    public final GriefHelper griefHelper;

    public final AutoExplosionFunction autoExplosionFunction;

    public final HitColor hitColor;
    public final FreeCam freeCam;
    public final GreifJoinerFunction greifJoinerFunction;
    public final ClickGui clickGui;
    public HUD2 hud2;
    public AspectRatio aspectRatio;
    public CrystalAura crystalAura;
    public TPAura tpAura;
    public Criticals criticals;
    public AutoPilot autoPilot;
    public Scaffold scaffold;
    public TargetStrafe targetStrafe;
    public FunctionManager() {
        // Инициализация и добавление функций в список modules
        this.functions.addAll(Arrays.asList(
                //RENDER
                clickGui = new ClickGui(),
                hud2 = new HUD2(),
                criticals = new Criticals(),
                targetStrafe = new TargetStrafe(),
                auraFunction = new Aura(),
                scaffold = new Scaffold(),
                crystalAura = new CrystalAura(),
                tpAura = new TPAura(),
                autoPilot = new AutoPilot(),
                noRenderFunction = new NoRenderFunction(),
                swingAnimationFunction = new SwingAnimationFunction(),
                item360 = new Item360(),
                aspectRatio = new AspectRatio(),
                itemPhysic = new ItemPhysic(),
                chinaHat = new ChinaHat(),
                elytraBoost = new ElytraBoost(),
                this.crosshair = new Crosshair(),
                this.greifJoinerFunction = new GreifJoinerFunction(),
                this.babyBoy = new BabyBoy(),
                this.griefHelper = new GriefHelper(),
                this.arrowsFunction = new ArrowsFunction(),
                this.fullBrightFunction = new FullBrightFunction(),
                this.sprintFunction = new SprintFunction(),
                this.flightFunction = new FlightFunction(),
                this.strafeFunction = new StrafeFunction(),
                this.streamMod = new StreamMod(),
                this.timerFunction = new TimerFunction(),
                this.buttonBloom = new ButtonBloom(),
                this.extendedTab = new ExtendedTab(),
                this.cameraSpec = new CameraSpec(),
                this.velocityFunction = new VelocityFunction(),
                this.middleClickPearlFunction = new MiddleClickPearlFunction(),
                this.autoTotemFunction = new AutoTotemFunction(),
                this.inventoryMoveFunction = new InventoryMoveFunction(),
                this.autoRespawnFunction = new AutoRespawnFunction(),
                noPushFunction = new NoPushFunction(),
                xbox = new Xbox(),
                noSlowFunction = new NoSlowFunction(),
                noServerRotFunction = new NoServerRotFunction(),
                fastBreakFunction = new FastBreakFunction(),
                autoPotionFunction = new AutoPotionFunction(),
                autoGApple = new AutoGAppleFunction(),
                gappleCooldownFunction = new GappleCooldownFunction(),
                optimization = new Optimization(),
                particleses = new Particles(),
                itemScroller = new ItemScroller(),
                nameTags = new NameTags(),
                noInteractFunction = new NoInteractFunction(),
                customWorld = new CustomWorld(),
                clientSounds = new ClientSounds(),
                nameProtect = new NameProtect(),
                hitColor = new HitColor(),
                noCommands = new NoCommands(),
                unhook = new UnHookFunction(),
                autoExplosionFunction = new AutoExplosionFunction(),
                freeCam = new FreeCam(),
                new WebServer(),
                new ElytraSwap(),
                new WaterSpeed(),
                new AutoTool(),
                new ChestStealer(),
                new AntiAim(),
                new ParticleTrails(),
                new AutoClanUpgrade(),
                new Tracers(),
                new NoFriendDamage(),
                new ItemESP(),
                new PearlPrediction(),
                new Step(),
                new AutoMyst(),
                new NoPitchLimit(),
                new KTLeaveFT(),
                new AutoTpacceptFunction(),
                new MiniBots(),
                new HighJump(),
                new MotionBlur(),
                new MiddleClickFriendFunction(),
                new ShulkerStealer(),
                new HoweLeave(),
                new LittleParrot(),
                new Spammer(),
                new GodMode(),
                new JumpCircleFunction(),
                new NoPlayerTrace(),
                new WebMove(),
                new PacketCancel(),
                new Disabler(),
                new AutoJump(),
                new NoDizEffect(),
                new BowSpammer(),
                new PoseRe(),
                new RCT(),
                new YawPotionHead(),
                new AutoParkour(),
                new AutoAncherFunction(),
                new SuperBow(),
                new TrailsFunction(),
                new TeleportItem(),
                new Eagle(),
                new BedBreaker(),
                new SpeedFunction(),
                new SnowRender(),
                new ElytraFly(),
                new AntiAFK(),
                new SeeBlockRay(),
                new AutoFarm(),
                new AntiBot(),
                new FireworkFly(),
                new ItemSwapFixFunction(),
                new DeathCoordsFunction(),
                new DragonFlyFunction(),
                new JesusFunction(),
                new ElytraFix(),
                new AutoSwap(),
                new ClickTP(),
                new Nuker(),
                new BedrockLeave(),
                new AutoDrinking(),
                new NPCEploit(),
                new CrystalOptimizer(),
                new SpiderFunction(),
                new NoClip(),
                new BlockESP(),
                new Blink(),
                new AttackMouse(),
                new AutoLeave(),
                new BackTrack(),
                new ClickSounds(),
                new Chams(),
                new NoDelay(),
                new AutoFisher(),
                new AutoEat(),
                new GlowESP(),
                new KTLeave(),
                new NoFall(),
                new Sneak(),
                new FastEXP(),
                new CustomHand(),
                new XCarry()
        ));
    }

    /**
     * Возвращает список всех функций.
     *
     * @return список функций.
     */
    public List<Function> getFunctions() {
        return functions;
    }

    public static Function get(String name) {
        for (Function function : functions) {
            if (function != null && function.name.equalsIgnoreCase(name)) {
                return function;
            }
        }
        return null;
    }
}
