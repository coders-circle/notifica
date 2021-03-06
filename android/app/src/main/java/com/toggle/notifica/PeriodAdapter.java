package com.toggle.notifica;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.toggle.notifica.database.DbHelper;
import com.toggle.notifica.database.Period;
import com.toggle.notifica.database.Subject;
import com.toggle.notifica.database.Teacher;

public class PeriodAdapter extends RecyclerView.Adapter<PeriodAdapter.PeriodViewHolder>{
    private List<Period> mPeriods;
    private List<Integer> mBreaks = new ArrayList<>();
    private Context mContext;

    public PeriodAdapter(Context context, List<Period> periods){
        mPeriods = periods;
        mContext = context;

        // Find out the break times
        if (mPeriods.size() > 1) {
            int cnt = 0;
            Period lastPeriod = mPeriods.get(0);
            for (int i=1; i<mPeriods.size(); ++i) {
                Period next = mPeriods.get(i);
                if (next.start_time > lastPeriod.end_time) {
                    mBreaks.add(i + cnt);
                    cnt++;
                }
                lastPeriod = next;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mPeriods.size() + mBreaks.size();
    }

    @Override
    public PeriodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PeriodViewHolder(LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.layout_period, parent, false));
    }

    @Override
    public void onBindViewHolder(PeriodViewHolder holder, int position) {
        // If break then show "Break"
        if (mBreaks.contains(position)) {
            holder.subject.setText("Break");
            holder.remarks.setVisibility(View.GONE);
            holder.time.setVisibility(View.GONE);
            holder.teachers.setVisibility(View.GONE);
            holder.subShortName.setVisibility(View.GONE);
            holder.subject.setGravity(Gravity.CENTER);
            holder.practicalMark.setVisibility(View.GONE);
            return;
        }

        // Else find the period and show that
        int pPosition = position;
        for (Integer b: mBreaks) {
            if (b < position)
                pPosition--;
            else if (b > position)
                break;
        }

        Period period = mPeriods.get(pPosition);

        /*boolean alternate = false;
        if (pPosition > 0) {
            Period lastPeriod = mPeriods.get(pPosition - 1);
            if (lastPeriod.start_time == period.start_time &&
                    lastPeriod.end_time == period.end_time)
                alternate = true;
        }

        // for alternate period hide the time
        holder.time.setVisibility(alternate?View.GONE:View.VISIBLE);*/

        // Get the subject and teachers
        DbHelper helper = new DbHelper(mContext);
        Subject subject = Subject.get(Subject.class, helper, period.subject);
        List<Teacher> teachers = period.getTeachers(helper);
        String teacher_str = "";

        for(int i = 0; i < teachers.size(); i++) {
            teacher_str += teachers.get(i).getUsername(helper);
            if (i != teachers.size() - 1) {
                teacher_str += ", ";
            }
        }

        String subShortName = subject.getShortName();

        // Set the period view contents

        holder.subShortName.setText(subShortName);
        holder.subShortName.setBackgroundResource(R.drawable.circle_filled);
        GradientDrawable shortNameBackground = (GradientDrawable) holder.subShortName.getBackground();
        shortNameBackground.setColor(Color.parseColor(subject.color));

        String timeText = period.getStartTime() + " - " + period.getEndTime();
        holder.time.setText(timeText);

        holder.subject.setText(subject.name);

        holder.teachers.setText(teacher_str);
        holder.teachers.setVisibility(teacher_str.length()==0?View.GONE:View.VISIBLE);

        holder.remarks.setText(period.remarks);
        holder.remarks.setVisibility(period.remarks.length()==0?View.GONE:View.VISIBLE);

        holder.practicalMark.setVisibility(period.period_type==1?View.VISIBLE:View.GONE);
    }

    public class PeriodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected TextView subShortName;
        protected TextView time;
        protected TextView subject;
        protected TextView teachers;
        protected TextView remarks;
        protected View practicalMark;

        public PeriodViewHolder(View v){
            super(v);
            v.setOnClickListener(this);
            subShortName = (TextView)v.findViewById(R.id.sub_shortname);
            time = (TextView)v.findViewById(R.id.time);
            subject = (TextView)v.findViewById(R.id.subject);
            teachers = (TextView)v.findViewById(R.id.teachers);
            remarks = (TextView)v.findViewById(R.id.remarks);
            practicalMark = v.findViewById(R.id.mark_practical);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
