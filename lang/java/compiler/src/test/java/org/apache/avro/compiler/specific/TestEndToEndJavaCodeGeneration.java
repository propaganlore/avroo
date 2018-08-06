package org.apache.avro.compiler.specific;

import org.apache.avro.compiler.testdata.UnionWithLogicalTypes;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;

public class TestEndToEndJavaCodeGeneration {

    @Test
    public void testWithNullValues() throws IOException {
        UnionWithLogicalTypes instanceOfGeneratedClass = UnionWithLogicalTypes.newBuilder()
                .setDateOrNull(null)
                .setStringOrNull("hello")
                .build();
        final ByteBuffer bb = instanceOfGeneratedClass.toByteBuffer();
        final UnionWithLogicalTypes copy = UnionWithLogicalTypes.fromByteBuffer(bb);
        Assert.assertEquals(instanceOfGeneratedClass.getDateOrNull(), copy.getDateOrNull());
        Assert.assertEquals(instanceOfGeneratedClass.getStringOrNull(), copy.getStringOrNull());
    }

    @Test
    public void testDate() throws IOException {
        UnionWithLogicalTypes instanceOfGeneratedClass = UnionWithLogicalTypes.newBuilder()
                .setDateOrNull(LocalDate.now())
                .setStringOrNull("hello")
                .build();
        final ByteBuffer bb = instanceOfGeneratedClass.toByteBuffer();
        final UnionWithLogicalTypes copy = UnionWithLogicalTypes.fromByteBuffer(bb);
        Assert.assertEquals(instanceOfGeneratedClass.getDateOrNull(), copy.getDateOrNull());
        Assert.assertEquals(instanceOfGeneratedClass.getStringOrNull(), copy.getStringOrNull());
    }

    @Test
    public void testDecimal() throws IOException {
        UnionWithLogicalTypes instanceOfGeneratedClass = UnionWithLogicalTypes.newBuilder()
                .setStringOrNull("hello")
                .setDecimalOrNull(BigDecimal.valueOf(123, 2))
                .build();
        final ByteBuffer bb = instanceOfGeneratedClass.toByteBuffer();
        final UnionWithLogicalTypes copy = UnionWithLogicalTypes.fromByteBuffer(bb);
        Assert.assertEquals(instanceOfGeneratedClass.getDecimalOrNull(), copy.getDecimalOrNull());
        Assert.assertEquals(instanceOfGeneratedClass.getStringOrNull(), copy.getStringOrNull());
    }

}
