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
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity


object AnimationManager {
    // Preference keys
    private const val PREFS_NAME = "AnimationSettings"
    private const val PREF_BACK_ANIMATION = "back_animation"
    private const val PREF_SWITCH_ANIMATION = "switch_animation"
    private const val PREF_BUTTON_ANIMATION = "button_animation"
    private const val PREF_PAGE_TRANSITION = "page_transition"
    private const val PREF_CARD_ANIMATION = "card_animation"


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
// CARD SELECTION ANIMATION
// -----------------------
    fun animateCardSelect(view: View, context: Context) {
        if (!getPreference(context, PREF_CARD_ANIMATION, true)) return

        view.animate()
            .translationY(-30f)
            .scaleX(1.05f)
            .scaleY(1.05f)
            .setDuration(150)
            .setInterpolator(OvershootInterpolator())
            .start()
    }

    fun animateCardDeselect(view: View, context: Context) {
        if (!getPreference(context, PREF_CARD_ANIMATION, true)) return

        view.animate()
            .translationY(0f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(150)
            .start()
    }

    // -----------------------
// CARD PLACEMENT (TO TABLE)
// -----------------------
    fun animateCardPlacement(
        view: View,
        context: Context,
        onEnd: (() -> Unit)? = null
    ) {
        if (!getPreference(context, PREF_CARD_ANIMATION, true)) {
            onEnd?.invoke()
            return
        }

        view.animate()
            .translationY(-200f)
            .alpha(0f)
            .setDuration(300)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                view.alpha = 1f
                view.translationY = 0f
                onEnd?.invoke()
            }
            .start()
    }

    // -----------------------
    // CARD DRAW ANIMATION
    // -----------------------
    fun animateCardDraw(view: View, context: Context) {
        if (!getPreference(context, PREF_CARD_ANIMATION, true)) return

        view.alpha = 0f
        view.translationY = 50f

        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(250)
            .start()
    }

    // -----------------------
    // CARD FLY (HAND → TABLE)
    // -----------------------
    fun animateCardFlyToTable(
        activity: AppCompatActivity,
        sourceView: ImageView,
        targetView: ImageView,
        drawableRes: Int,
        onEnd: () -> Unit
    ) {
        if (!getPreference(activity, PREF_CARD_ANIMATION, true)) {
            onEnd()
            return
        }

        val root = activity.window.decorView as ViewGroup

        val startPos = IntArray(2)
        val endPos = IntArray(2)

        sourceView.getLocationOnScreen(startPos)
        targetView.getLocationOnScreen(endPos)

        val flyingCard = ImageView(activity).apply {
            setImageResource(drawableRes)
            layoutParams = ViewGroup.LayoutParams(
                sourceView.width,
                sourceView.height
            )
            x = startPos[0].toFloat()
            y = startPos[1].toFloat()
            elevation = 100f
        }

        root.addView(flyingCard)

        flyingCard.animate()
            .x(endPos[0].toFloat())
            .y(endPos[1].toFloat())
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(350)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                root.removeView(flyingCard)
                onEnd()
            }
            .start()
    }



    // -----------------------
    // HELPER KEYS (for settings)
    // -----------------------
    fun getBackAnimationKey() = PREF_BACK_ANIMATION
    fun getSwitchAnimationKey() = PREF_SWITCH_ANIMATION
    fun getButtonAnimationKey() = PREF_BUTTON_ANIMATION
    fun getPageTransitionKey() = PREF_PAGE_TRANSITION
    fun getCardAnimationKey() = PREF_CARD_ANIMATION

}