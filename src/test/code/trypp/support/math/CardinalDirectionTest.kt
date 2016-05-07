package trypp.support.math

import com.google.common.truth.Truth.assertThat
import org.testng.annotations.Test

class CardinalDirectionTest {
    @Test fun getForAngleWorks() {
        assertThat(CardinalDirection.getForAngle(Angle.fromDegrees(0f))).isEqualTo(
            CardinalDirection.E)
        assertThat(CardinalDirection.getForAngle(Angle.fromDegrees(45f))).isEqualTo(
            CardinalDirection.N)
        assertThat(CardinalDirection.getForAngle(Angle.fromDegrees(90f))).isEqualTo(
            CardinalDirection.N)
        assertThat(CardinalDirection.getForAngle(Angle.fromDegrees(135f))).isEqualTo(
            CardinalDirection.W)
        assertThat(CardinalDirection.getForAngle(Angle.fromDegrees(180f))).isEqualTo(
            CardinalDirection.W)
        assertThat(CardinalDirection.getForAngle(Angle.fromDegrees(225f))).isEqualTo(
            CardinalDirection.S)
        assertThat(CardinalDirection.getForAngle(Angle.fromDegrees(270f))).isEqualTo(
            CardinalDirection.S)
        assertThat(CardinalDirection.getForAngle(Angle.fromDegrees(315f))).isEqualTo(
            CardinalDirection.E)
    }

    @Test fun angleIsCorrect() {
        assertThat(CardinalDirection.E.angle.getDegrees()).isWithin(0f).of(0f)
        assertThat(CardinalDirection.N.angle.getDegrees()).isWithin(0f).of(90f)
        assertThat(CardinalDirection.W.angle.getDegrees()).isWithin(0f).of(180f)
        assertThat(CardinalDirection.S.angle.getDegrees()).isWithin(0f).of(270f)
    }

    @Test fun directionFacingWorks() {
        assertThat(CardinalDirection.E.faces(Angle.fromDegrees(10f))).isTrue()
        assertThat(CardinalDirection.S.faces(Angle.fromDegrees(180f))).isFalse()
        assertThat(CardinalDirection.E.faces(Angle.fromDegrees(350f))).isTrue()
    }
}