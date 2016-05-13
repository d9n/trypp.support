package trypp.support.math

import com.google.common.truth.Truth.assertThat
import org.testng.annotations.Test

class CompassDirectionTest {
    @Test fun getForAngleWorks() {
        assertThat(CompassDirection.getForAngle(Angle.ofDegrees(0f))).isEqualTo(
            CompassDirection.E)
        assertThat(CompassDirection.getForAngle(Angle.ofDegrees(45f))).isEqualTo(
            CompassDirection.NE)
        assertThat(CompassDirection.getForAngle(Angle.ofDegrees(90f))).isEqualTo(
            CompassDirection.N)
        assertThat(CompassDirection.getForAngle(Angle.ofDegrees(135f))).isEqualTo(
            CompassDirection.NW)
        assertThat(CompassDirection.getForAngle(Angle.ofDegrees(180f))).isEqualTo(
            CompassDirection.W)
        assertThat(CompassDirection.getForAngle(Angle.ofDegrees(225f))).isEqualTo(
            CompassDirection.SW)
        assertThat(CompassDirection.getForAngle(Angle.ofDegrees(270f))).isEqualTo(
            CompassDirection.S)
        assertThat(CompassDirection.getForAngle(Angle.ofDegrees(315f))).isEqualTo(
            CompassDirection.SE)
    }

    @Test fun angleIsCorrect() {
        assertThat(CompassDirection.E.angle.getDegrees()).isWithin(0f).of(0f)
        assertThat(CompassDirection.N.angle.getDegrees()).isWithin(0f).of(90f)
        assertThat(CompassDirection.W.angle.getDegrees()).isWithin(0f).of(180f)
        assertThat(CompassDirection.S.angle.getDegrees()).isWithin(0f).of(270f)
    }

    @Test fun directionFacingWorks() {
        assertThat(CompassDirection.E.faces(Angle.ofDegrees(10f))).isTrue()
        assertThat(CompassDirection.S.faces(Angle.ofDegrees(180f))).isFalse()
        assertThat(CompassDirection.E.faces(Angle.ofDegrees(350f))).isTrue()
    }
}