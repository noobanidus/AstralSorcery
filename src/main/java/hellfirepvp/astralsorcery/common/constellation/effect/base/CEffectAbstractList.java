/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.base;

import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.BlockPredicate;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectAbstractList
 * Created by HellFirePvP
 * Date: 11.06.2019 / 19:59
 */
public abstract class CEffectAbstractList<T extends CEffectAbstractList.ListEntry> extends ConstellationEffect {

    protected final BlockPredicate verifier;
    protected final int maxAmount;
    private List<T> elements = new ArrayList<>();

    protected CEffectAbstractList(@Nullable ILocatable origin, IWeakConstellation cst, int maxAmount, BlockPredicate verifier) {
        super(origin, cst);
        this.maxAmount = maxAmount;
        this.verifier = verifier;
    }

    public abstract T createElement(@Nullable World world, BlockPos pos);

    public int getCount() {
        return this.elements.size();
    }

    public void clear() {
        this.elements.clear();
    }

    @Nullable
    public T getRandomElement() {
        return this.elements.isEmpty() ? null : this.elements.get(rand.nextInt(this.getCount()));
    }

    @Nullable
    public T getRandomElementChanced() {
        if (this.elements.isEmpty()) {
            return null;
        }
        if (rand.nextInt(Math.max(0, (this.maxAmount - this.getCount()) / 4) + 1) == 0) {
            return getRandomElement();
        }
        return null;
    }

    public boolean findNewPosition(World world, BlockPos pos, ConstellationEffectProperties prop) {
        if (this.getCount() >= this.maxAmount) {
            return false;
        }
        double range = prop.getSize();
        double rX = -range + rand.nextFloat() * (2 * range + 1);
        double rY = -range + rand.nextFloat() * (2 * range + 1);
        double rZ = -range + rand.nextFloat() * (2 * range + 1);
        BlockPos at = pos.add(rX, rY, rZ);
        if (MiscUtils.isChunkLoaded(world, at) && this.verifier.test(world, at, world.getBlockState(at)) && !this.hasElement(at)) {
            T newElement = this.createElement(world, at);
            if (newElement != null) {
                this.elements.add(newElement);
                return true;
            }
        }

        return false;
    }

    public boolean removeElement(BlockPos pos) {
        return this.elements.removeIf(e -> e.getPos().equals(pos));
    }

    public boolean hasElement(BlockPos pos) {
        return MiscUtils.contains(this.elements, e -> e.getPos().equals(pos));
    }

    @Override
    public void readFromNBT(CompoundNBT cmp) {
        super.readFromNBT(cmp);

        this.elements.clear();

        ListNBT list = cmp.getList("elements", Constants.NBT.TAG_COMPOUND);
        for (INBT nbt : list) {
            CompoundNBT tag = (CompoundNBT) nbt;
            BlockPos pos = NBTHelper.readBlockPosFromNBT(tag);
            T element = this.createElement(null, pos);
            if (element != null) {
                element.readFromNBT(tag.getCompound("data"));
                this.elements.add(element);
            }
        }
    }

    @Override
    public void writeToNBT(CompoundNBT cmp) {
        super.writeToNBT(cmp);

        ListNBT list = new ListNBT();
        for (T element : this.elements) {
            CompoundNBT tag = new CompoundNBT();
            NBTHelper.writeBlockPosToNBT(element.getPos(), tag);

            CompoundNBT dataTag = new CompoundNBT();
            element.writeToNBT(dataTag);
            tag.put("data", dataTag);

            list.add(tag);
        }
        cmp.put("elements", list);
    }

    public static interface ListEntry {

        public BlockPos getPos();

        public void writeToNBT(CompoundNBT nbt);

        public void readFromNBT(CompoundNBT nbt);

    }

}
