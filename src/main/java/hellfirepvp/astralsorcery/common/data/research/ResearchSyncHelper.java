/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.research;

import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncKnowledge;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ResearchSyncHelper
 * Created by HellFirePvP
 * Date: 21.04.2019 / 19:20
 */
public class ResearchSyncHelper {

    public static void pushProgressToClientUnsafe(PlayerProgress progress, PlayerEntity p) {
        PktSyncKnowledge pkt = new PktSyncKnowledge(PktSyncKnowledge.STATE_ADD);
        pkt.load(progress);
        PacketChannel.CHANNEL.sendToPlayer(p, pkt);
    }

    @OnlyIn(Dist.CLIENT)
    public static void recieveProgressFromServer(PktSyncKnowledge message, PlayerEntity player) {
        int currentLvl = ResearchHelper.getClientProgress().getPerkLevel(player);
        ResearchHelper.updateClientResearch(message);
        if (ResearchHelper.getClientProgress().getPerkLevel(player) > currentLvl) {
            //TODO display client charge
            //ClientRenderEventHandler.requestPermChargeReveal(160);
        }
    }

}
