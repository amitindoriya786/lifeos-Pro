package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.LifeOSRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read app_name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("LifeOS Pro", appName)
  }

  @Test
  fun `repository loads default 20 questions and initial state`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = LifeOSRepository(context)
    val state = repository.state.value

    assertNotNull(state)
    assertEquals(20, state.questions.size)
    assertTrue(state.tasks.isNotEmpty())
    assertTrue(state.targets.isNotEmpty())
    assertTrue(state.habits.isNotEmpty())
    assertEquals(78, state.profile.disciplineScore)
  }

  @Test
  fun `eisenhower matrix tasks can be added and moved across quadrants`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = LifeOSRepository(context)

    repository.addTask(
      title = "Urgent Client Delivery",
      priority = "P1",
      leverage = "10k",
      energy = "high",
      quadrant = "Q1"
    )

    val added = repository.state.value.tasks.first { it.title == "Urgent Client Delivery" }
    assertEquals("Q1", added.quadrant)

    // Move to Q2
    repository.moveTaskQuadrant(added.id, "Q2")
    val moved = repository.state.value.tasks.first { it.id == added.id }
    assertEquals("Q2", moved.quadrant)
  }

  @Test
  fun `future goals load with ideal benchmarks and can be updated`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = LifeOSRepository(context)
    val goals = repository.state.value.futureGoals

    assertTrue(goals.isNotEmpty())
    val firstGoal = goals.first()
    assertNotNull(firstGoal.idealMetric)
    assertNotNull(firstGoal.gapSummary)
    assertNotNull(firstGoal.bridgeTheGapAction)

    val initialCount = firstGoal.completedMilestonesCount
    repository.toggleGoalMilestone(firstGoal.id)
    val updatedGoal = repository.state.value.futureGoals.first { it.id == firstGoal.id }
    assertTrue(updatedGoal.completedMilestonesCount != initialCount || updatedGoal.keyMilestones.isEmpty())
  }
}
