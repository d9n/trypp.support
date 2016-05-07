package trypp.support.math

import com.google.common.truth.Truth.assertThat
import org.testng.annotations.Test

class CompassDirectionTest {
    @Test fun getForAngleWorks() {
        assertThat(CompassDirection.getForAngle(Angle.fromDegrees(0f))).isEqualTo(
            CompassDirection.E)
        assertThat(CompassDirection.getForAngle(Angle.fromDegrees(45f))).isEqualTo(
            CompassDirection.NE)
        assertThat(CompassDirection.getForAngle(Angle.fromDegrees(90f))).isEqualTo(
            CompassDirection.N)
        assertThat(CompassDirection.getForAngle(Angle.fromDegrees(135f))).isEqualTo(
            CompassDirection.NW)
        assertThat(CompassDirection.getForAngle(Angle.fromDegrees(180f))).isEqualTo(
            CompassDirection.W)
        assertThat(CompassDirection.getForAngle(Angle.fromDegrees(225f))).isEqualTo(
            CompassDirection.SW)
        assertThat(CompassDirection.getForAngle(Angle.fromDegrees(270f))).isEqualTo(
            CompassDirection.S)
        assertThat(CompassDirection.getForAngle(Angle.fromDegrees(315f))).isEqualTo(
            CompassDirection.SE)
    }

    @Test fun angleIsCorrect() {
        assertThat(CompassDirection.E.angle.getDegrees()).isWithin(0f).of(0f)
        assertThat(CompassDirection.N.angle.getDegrees()).isWithin(0f).of(90f)
        assertThat(CompassDirection.W.angle.getDegrees()).isWithin(0f).of(180f)
        assertThat(CompassDirection.S.angle.getDegrees()).isWithin(0f).of(270f)
    }

    @Test fun directionFacingWorks() {
        assertThat(CompassDirection.E.faces(Angle.fromDegrees(10f))).isTrue()
        assertThat(CompassDirection.S.faces(Angle.fromDegrees(180f))).isFalse()
        assertThat(CompassDirection.E.faces(Angle.fromDegrees(350f))).isTrue()
    }
}