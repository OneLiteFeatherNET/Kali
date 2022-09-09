package net.theevilreaper.dungeon.database.codec;

import net.minestom.server.item.Material;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.jetbrains.annotations.NotNull;

/**
 * Custom codec for the {@link Material}.
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class MaterialCodec implements Codec<Material> {

    /**
     * Decodes a {@link Material} from a {@link BsonReader} instance.
     * @param reader         the BSON reader
     * @param decoderContext the decoder context
     * @return the decoded material
     */
    @Override
    public Material decode(@NotNull BsonReader reader, DecoderContext decoderContext) {
        return Material.fromNamespaceId(reader.readString());
    }

    /**
     * Writes the {@link Material} into the {@link BsonWriter}.
     * @param writer the BSON writer to encode into
     * @param value the value to encode
     * @param encoderContext the encoder context
     */
    @Override
    public void encode(@NotNull BsonWriter writer, @NotNull Material value, EncoderContext encoderContext) {
        writer.writeString(value.name());
    }

    /**
     * Returns the encoder class.
     * @return the {@link Material} class
     */
    @Override
    public Class<Material> getEncoderClass() {
        return Material.class;
    }
}
