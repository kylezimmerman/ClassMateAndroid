/*
    EventItemView.java

    This class handles display of each item in the Event feed ViewList.

    Kyle Zimmerman, Justin Coschi, Sean Coombes
 */

package com.prog3210.classmate.events;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prog3210.classmate.LogHelper;
import com.prog3210.classmate.R;
import com.prog3210.classmate.courses.Course;

public class EventItemView extends LinearLayout {

    private TextView courseTextView;
    private TextView eventNameTextView;
    private TextView dueDateTextView;

    private TextView upvoteCountTextView;
    private TextView downvoteCountTextView;

    private LinearLayout voteBar;

    public EventItemView(Context context) {
        super(context);
    }

    public EventItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        // 'getViews()' can't throw an Exception because it is caught in the method
        super.onFinishInflate();
        getViews();
    }

    public EventItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // Gets the objects displayed in the layout for use.
    private void getViews() {
        try {
            courseTextView = (TextView) findViewById(R.id.course);
            eventNameTextView = (TextView) findViewById(R.id.event_name);
            dueDateTextView = (TextView) findViewById(R.id.due_date);

            upvoteCountTextView = (TextView) findViewById(R.id.upvote_count);
            downvoteCountTextView = (TextView) findViewById(R.id.downvote_count);

            voteBar = (LinearLayout) findViewById(R.id.vote_bar);
        } catch (Exception e) {
            LogHelper.logError(getContext(), "EventItemView", "Error displaying Events.", e.getMessage());
        }
    }

    /***
     * Updates the Event layout to display updated information for the current Event.
     * @param event Current Event being viewed.
     */
    public void update(Event event) {
        try {
            Course course = event.getCourse();
            if (course != null) {
                courseTextView.setText(String.format("%s - %s", course.getCourseCode(), course.getName()));
            } else {
                courseTextView.setText("");
            }
            eventNameTextView.setText(event.getName());
            dueDateTextView.setText(event.getDateString());

            // Gets the vote values for an Event.
            int upvotes = event.getUpvotes();
            int downvotes = event.getDownvotes();

            // Sets the visibility and display of the votes for an Event.
            if (upvotes == 0 && downvotes == 0) {
                voteBar.setVisibility(View.GONE);
            } else {
                voteBar.setVisibility(View.VISIBLE);
                upvoteCountTextView.setText(String.valueOf(upvotes));
                downvoteCountTextView.setText(String.valueOf(downvotes));
            }
        } catch (Exception e) {
            LogHelper.logError(getContext(), "EventItemView", "Error updating event.", e.getMessage());
        }
    }
}
