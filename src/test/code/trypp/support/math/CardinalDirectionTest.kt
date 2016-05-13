package trypp.support.math

import com.google.common.truth.Truth.assertThat
import org.testng.annotations.Test

class CardinalDirectionTest {
    @Test fun getForAngleWorks() {
        assertThat(CardinalDirection.getForAngle(Angle.ofDegrees(0f))).isEqualTo(
            CardinalDirection.E)
        assertThat(CardinalDirection.getForAngle(Angle.ofDegrees(45f))).isEqualTo(
            CardinalDirection.N)
        assertThat(CardinalDirection.getForAngle(Angle.ofDegrees(90f))).isEqualTo(
            CardinalDirection.N)
        assertThat(CardinalDirection.getForAngle(Angle.ofDegrees(135f))).isEqualTo(
            CardinalDirection.W)
        assertThat(CardinalDirection.getForAngle(Angle.ofDegrees(180f))).isEqualTo(
            CardinalDirection.W)
        assertThat(CardinalDirection.getForAngle(Angle.ofDegrees(225f))).isEqualTo(
            CardinalDirection.S)
        assertThat(CardinalDirection.getForAngle(Angle.ofDegrees(270f))).isEqualTo(
            CardinalDirection.S)
        assertThat(CardinalDirection.getForAngle(Angle.ofDegrees(315f))).isEqualTo(
            CardinalDirection.E)
    }

    @Test fun angleIsCorrect() {
        assertThat(CardinalDirection.E.angle.getDegrees()).isWithin(0f).of(0f)
        assertThat(CardinalDirection.N.angle.getDegrees()).isWithin(0f).of(90f)
        assertThat(CardinalDirection.W.angle.getDegrees()).isWithin(0f).of(180f)
        assertThat(CardinalDirection.S.angle.getDegrees()).isWithin(0f).of(270f)
    }

    @Test fun directionFacingWorks() {
        assertThat(CardinalDirection.E.faces(Angle.ofDegrees(10f))).isTrue()
        assertThat(CardinalDirection.S.faces(Angle.ofDegrees(180f))).isFalse()
        assertThat(CardinalDirection.E.faces(Angle.ofDegrees(350f))).isTrue()
    }
}