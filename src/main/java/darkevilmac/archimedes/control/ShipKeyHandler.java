package darkevilmac.archimedes.control;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import darkevilmac.archimedes.ArchimedesConfig;
import darkevilmac.archimedes.ArchimedesShipMod;
import darkevilmac.archimedes.entity.EntityShip;
import darkevilmac.archimedes.network.ClientOpenGuiMessage;
import darkevilmac.movingworld.MovingWorld;
import darkevilmac.movingworld.network.MovingWorldClientActionMessage;

@SideOnly(Side.CLIENT)
public class ShipKeyHandler {
    private ArchimedesConfig config;
    private boolean kbShipGuiPrevState, kbDisassemblePrevState;

    public ShipKeyHandler(ArchimedesConfig cfg) {
        config = cfg;
        kbShipGuiPrevState = kbDisassemblePrevState = false;

    }

    @SubscribeEvent
    public void keyPress(InputEvent.KeyInputEvent e) {
    }

    @SubscribeEvent
    public void updateControl(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.START && e.side == Side.CLIENT && e.player == FMLClientHandler.instance().getClientPlayerEntity() && e.player.ridingEntity != null && e.player.ridingEntity instanceof EntityShip) {
            if (config.kbShipInv.getIsKeyPressed() && !kbShipGuiPrevState) {
                ClientOpenGuiMessage msg = new ClientOpenGuiMessage(2);
                ArchimedesShipMod.instance.network.sendToServer(msg);
            }
            kbShipGuiPrevState = config.kbShipInv.getIsKeyPressed();

            if (config.kbDisassemble.getIsKeyPressed() && !kbDisassemblePrevState) {
                MovingWorldClientActionMessage msg = new MovingWorldClientActionMessage((EntityShip) e.player.ridingEntity, MovingWorldClientActionMessage.Action.DISASSEMBLE);
                MovingWorld.instance.network.sendToServer(msg);
            }
            kbDisassemblePrevState = config.kbDisassemble.getIsKeyPressed();

            int c = getHeightControl();
            EntityShip ship = (EntityShip) e.player.ridingEntity;
            if (c != ship.getController().getShipControl()) {
                ship.getController().updateControl(ship, e.player, c);
            }
        }
    }


    public int getHeightControl() {
        if (config.kbAlign.getIsKeyPressed()) return 4;
        if (config.kbBrake.getIsKeyPressed()) return 3;
        int vert = 0;
        if (config.kbUp.getIsKeyPressed()) vert++;
        if (config.kbDown.getIsKeyPressed()) vert--;
        return vert == 0 ? 0 : vert < 0 ? 1 : vert > 0 ? 2 : 0;
    }
}
