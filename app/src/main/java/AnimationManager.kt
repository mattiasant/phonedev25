package ut.cs.ee.phonedev25

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.Switch
import android.widget.ImageView

object AnimationManager {
    // Preference keys
    private const val PREFS_NAME = "AnimationSettings"
    private const val PREF_BACK_ANIMATION = "back_animation"
    private const val PREF_SWITCH_ANIMATION = "switch_animation"
    private const val PREF_BUTTON_ANIMATION = "button_animation"
    private const val PREF_PAGE_TRANSITION = "page_transition"

    // -----------------------
    // PREFERENCES
    // -----------------------
    private fun getPrefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setPreference(context: Context, key: String, value: Boolean) {
        getPrefs(context).edit().putBoolean(key, value).apply()
    }

    fun getPreference(context: Context, key: String, defaultValue: Boolean = true): Boolean {
        return getPrefs(context).getBoolean(key, defaultValue)
    }

    // -----------------------
    // BUTTON CLICK ANIMATION
    // -----------------------
    fun animateButtonClick(view: View, context: Context) {
        if (getPreference(context, PREF_BUTTON_ANIMATION, true)) {
            view.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction {
                    view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()
        }
    }

    // -----------------------
    // SWITCH ANIMATION
    // -----------------------
    fun animateSwitch(switch: Switch, context: Context) {
        if (getPreference(context, PREF_SWITCH_ANIMATION, true)) {
            switch.animate()
                .scaleX(1.1f)
                .scaleY(1.1f)
                .setDuration(150)
                .withEndAction {
                    switch.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(150)
                        .start()
                }
                .start()
        }
    }

    // -----------------------
    // BACK BUTTON ANIMATION
    // -----------------------
    fun animateBackButton(view: ImageView, context: Context, onEnd: () -> Unit) {
        if (getPreference(context, PREF_BACK_ANIMATION, true)) {
            val scaleDown = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f)
            val scaleUp = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f)
            scaleDown.duration = 100
            scaleUp.duration = 100
            scaleDown.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    scaleUp.start()
                }
            })
            scaleDown.start()

            view.animate()
                .rotation(360f)
                .setDuration(300)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction {
                    view.rotation = 0f
                    onEnd()
                }
                .start()
        } else {
            onEnd()
        }
    }

    // -----------------------
    // ENTRANCE ANIMATION
    // -----------------------
    fun animateEntrance(view: View) {
        view.alpha = 0f
        view.translationY = 50f
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    // -----------------------
    // PAGE TRANSITION
    // -----------------------
    fun applyPageTransition(context: Context, enterAnim: Int, exitAnim: Int) {
        if (getPreference(context, PREF_PAGE_TRANSITION, true)) {
            if (context is android.app.Activity) {
                context.overridePendingTransition(enterAnim, exitAnim)
            }
        }
    }

    // -----------------------
    // HELPER KEYS (for settings)
    // -----------------------
    fun getBackAnimationKey() = PREF_BACK_ANIMATION
    fun getSwitchAnimationKey() = PREF_SWITCH_ANIMATION
    fun getButtonAnimationKey() = PREF_BUTTON_ANIMATION
    fun getPageTransitionKey() = PREF_PAGE_TRANSITION
}