package trypp.support.math

import com.google.common.truth.Truth.assertThat
import org.testng.annotations.Test


class AngleTest {

    @Test fun testSetAngleToDegrees() {
        val angle = Angle.fromDegrees(45f)
        assertThat(angle.getDegrees()).isWithin(0f).of(45f)
        assertThat(angle.getRadians()).isWithin(.1f).of(45f * Angle.DEG_TO_RAD)
    }

    @Test fun testSetAngleToRadians() {
        val angle = Angle.fromRadians(Angle.PI / 3f)
        assertThat(angle.getRadians()).isWithin(0f).of(Angle.PI / 3f)
        assertThat(angle.getDegrees()).isWithin(.1f).of(Angle.PI / 3f * Angle.RAD_TO_DEG)
    }

    @Test fun testSetAngleToOtherAngle() {
        val angle = Angle.fromDegrees(0f)
        val otherAngle = Angle.fromDegrees(45f)
        assertThat(angle.getDegrees()).isWithin(0f).of(0f)
        angle.setFrom(otherAngle)
        assertThat(angle.getDegrees()).isWithin(0f).of(45f)
    }

    @Test fun testInitAngleFromOtherAngle() {
        val otherAngle = Angle.fromDegrees(45f)
        val angle = Angle.from(otherAngle)
        assertThat(angle.getDegrees()).isWithin(0f).of(45f)
    }

    @Test fun testSetAngleToDegreesThenRadians() {
        val angle = Angle.fromDegrees(180f)
        angle.setRadians(Angle.PI / 2f)
        assertThat(angle.getDegrees()).isWithin(.1f).of(90f)
    }

    @Test fun testSetAngleToRadiansThenDegrees() {
        val angle = Angle.fromRadians(Angle.PI)
        angle.setDegrees(90f)
        assertThat(angle.getRadians()).isWithin(.1f).of(Angle.PI / 2)
    }

    @Test fun outOfBoundsDegreesAreBounded() {
        val angle = Angle.fromDegrees(0f)

        angle.setDegrees(-300f)
        assertThat(angle.getDegrees()).isWithin(0f).of(60f)

        angle.setDegrees(-3000f)
        assertThat(angle.getDegrees()).isWithin(0f).of(240f)

        angle.setDegrees(400f)
        assertThat(angle.getDegrees()).isWithin(0f).of(40f)

        angle.setDegrees(2000f)
        assertThat(angle.getDegrees()).isWithin(0f).of(200f)
    }

    @Test fun outOfBoundsRadiansAreBounded() {
        val angle = Angle.fromRadians(0f)

        angle.setRadians(-Angle.PI / 2f)
        assertThat(angle.getRadians()).isWithin(.1f).of(3f * Angle.PI / 2f)

        angle.setRadians(-5f * Angle.PI)
        assertThat(angle.getRadians()).isWithin(.1f).of(Angle.PI)

        angle.setRadians(5f * Angle.PI / 2f)
        assertThat(angle.getRadians()).isWithin(.1f).of(Angle.PI / 2f)

        angle.setRadians(31f * Angle.PI)
        assertThat(angle.getRadians()).isWithin(.1f).of(Angle.PI)
    }

    @Test fun addDegreesWorks() {
        val angle = Angle.fromDegrees(0f)
        assertThat(angle.getDegrees()).isWithin(0f).of(0f)

        angle.addDegrees(200f)
        assertThat(angle.getDegrees()).isWithin(0f).of(200f)

        angle.addDegrees(200f)
        assertThat(angle.getDegrees()).isWithin(0f).of(40f)

        angle.addDegrees(720f)
        assertThat(angle.getDegrees()).isWithin(0f).of(40f)
    }

    @Test fun addRadiansWorks() {
        val angle = Angle.fromRadians(0f)
        assertThat(angle.getRadians()).isWithin(0f).of(0f)

        angle.addRadians(Angle.PI)
        assertThat(angle.getRadians()).isWithin(0f).of(Angle.PI)

        angle.addRadians(3 * Angle.HALF_PI)
        assertThat(angle.getRadians()).isWithin(.1f).of(Angle.HALF_PI)

        angle.addRadians(Angle.TWO_PI)
        assertThat(angle.getRadians()).isWithin(.1f).of(Angle.HALF_PI)
    }

    @Test fun addAngleWorks() {
        val angle1 = Angle.fromDegrees(0f)
        val angle2 = Angle.fromDegrees(100f)

        angle1.add(angle2)
        assertThat(angle1.getDegrees()).isWithin(0f).of(100f)
    }

    @Test fun subDegreesWorks() {
        val angle = Angle.fromDegrees(300f)
        assertThat(angle.getDegrees()).isWithin(0f).of(300f)

        angle.subDegrees(200f)
        assertThat(angle.getDegrees()).isWithin(0f).of(100f)

        angle.subDegrees(200f)
        assertThat(angle.getDegrees()).isWithin(0f).of(260f)

        angle.subDegrees(720f)
        assertThat(angle.getDegrees()).isWithin(0f).of(260f)
    }

    @Test fun subRadiansWorks() {
        val angle = Angle.fromRadians(3 * Angle.QUARTER_PI)
        assertThat(angle.getRadians()).isWithin(.1f).of(3 * Angle.QUARTER_PI)

        angle.subRadians(Angle.HALF_PI)
        assertThat(angle.getRadians()).isWithin(.1f).of(Angle.QUARTER_PI)

        angle.subRadians(Angle.PI)
        assertThat(angle.getRadians()).isWithin(.1f).of(Angle.PI + Angle.QUARTER_PI)

        angle.subRadians(Angle.TWO_PI)
        assertThat(angle.getRadians()).isWithin(.1f).of(Angle.PI + Angle.QUARTER_PI)
    }

    @Test fun subAngleWorks() {
        val angle1 = Angle.fromDegrees(300f)
        val angle2 = Angle.fromDegrees(100f)

        angle1.sub(angle2)
        assertThat(angle1.getDegrees()).isWithin(0f).of(200f)
    }

}