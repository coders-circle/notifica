package com.lipi.notifica.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

// The SQLite database handler class
public class DbHelper extends SQLiteOpenHelper {

    public final static String[] DAYS = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

    // Database name and version
    public static final String DB_NAME = "Notifica.db";
    public static final int DB_VERSION = 5;

    // Create the helper object
    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Create all tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Classroom
        db.execSQL(new Organization().getCreateTableSql());
        db.execSQL(new Department().getCreateTableSql());
        db.execSQL(new Subject().getCreateTableSql());
        db.execSQL(new Teacher().getCreateTableSql());
        db.execSQL(new PClass().getCreateTableSql());
        db.execSQL(new PGroup().getCreateTableSql());
        db.execSQL(new Student().getCreateTableSql());
        db.execSQL(new User().getCreateTableSql());
        db.execSQL(new Profile().getCreateTableSql());

        // Routine
        db.execSQL(new Period().getCreateTableSql());
        db.execSQL(new PeriodGroup().getCreateTableSql());
        db.execSQL(new PeriodTeacher().getCreateTableSql());

        // Feed
        db.execSQL(new Post().getCreateTableSql());
        db.execSQL(new Comment().getCreateTableSql());
    }

    // Destroy and re-create all tables
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Classroom
        db.execSQL(new Organization().getDestroyTableSql());
        db.execSQL(new Department().getDestroyTableSql());
        db.execSQL(new Subject().getDestroyTableSql());
        db.execSQL(new Teacher().getDestroyTableSql());
        db.execSQL(new PClass().getDestroyTableSql());
        db.execSQL(new PGroup().getDestroyTableSql());
        db.execSQL(new Student().getDestroyTableSql());
        db.execSQL(new User().getDestroyTableSql());
        db.execSQL(new Profile().getDestroyTableSql());

        // Routine
        db.execSQL(new Period().getDestroyTableSql());
        db.execSQL(new PeriodGroup().getDestroyTableSql());
        db.execSQL(new PeriodTeacher().getDestroyTableSql());

        // Feed
        db.execSQL(new Post().getDestroyTableSql());
        db.execSQL(new Comment().getDestroyTableSql());

        onCreate(db);
    }

    // Delete all records that are useless
    // These include non-referenced subject, teacher, student, group, class, etc.

    public void deleteUseless() {
        // We should be careful of the order

        // Students
        // TODO

        // Teachers
        String tlist = "(";
        boolean first = true;

        List<PeriodTeacher> periodTeachers = PeriodTeacher.getAll(PeriodTeacher.class, this);
        for (PeriodTeacher pt: periodTeachers) {
            if (first) {
                first = false;
                tlist += pt.teacher;
            } else
                tlist += ", " + pt.teacher;
        }

        tlist += ")";
        if (tlist.equals("()"))
            Teacher.deleteAll(Teacher.class, this);
        else
            Teacher.delete(Teacher.class, this, "_id NOT IN " + tlist, null);


        // Subjects
        String subList = "(";
        first = true;

        List<Period> periods = Period.getAll(Period.class, this);
        for (Period p: periods) {
            if (first) {
                first = false;
                subList += p.subject;
            } else
                subList += ", " + p.subject;
        }

        subList += ")";
        if (subList.equals("()"))
            Subject.deleteAll(Subject.class, this);
        else
            Subject.delete(Subject.class, this, "_id NOT IN " + subList, null);


        // Groups
        String gList = "(";
        first = true;

        List<PeriodGroup> periodGroups = PeriodGroup.getAll(PeriodGroup.class, this);
        for (PeriodGroup pt: periodGroups) {
            if (first) {
                first = false;
                gList += pt.p_group;
            } else
                gList += ", " + pt.p_group;
        }

        List<Student> students = Student.getAll(Student.class, this);
        for (Student st: students) {
            if (first) {
                first = false;
                gList += st.p_group;
            } else
                gList += ", " + st.p_group;
        }

        gList += ")";
        if (gList.equals("()"))
            PGroup.deleteAll(PGroup.class, this);
        else
            PGroup.delete(PGroup.class, this, "_id NOT IN " + gList, null);


        // Classes
        String cList = "(";
        first = true;

        List<PGroup> groups = PGroup.getAll(PGroup.class, this);
        for (PGroup pg: groups) {
            if (first) {
                first = false;
                cList += pg.p_class;
            } else
                cList += ", " + pg.p_class;
        }

        cList += ")";
        if (cList.equals("()"))
            PClass.deleteAll(PClass.class, this);
        else
            PClass.delete(PClass.class, this, "_id NOT IN " + cList, null);


        // TODO: Users
    }

    // Delete all profiles except 'keep' recent entries
    public void deleteProfiles(int keep) {
        List<Profile> profiles = Profile.getAll(Profile.class, this, "downloaded_at");
        String plist = "(";
        for (int i=keep;i<profiles.size(); ++i) {
            if (i!=keep)
                plist += ", ";
            plist += profiles.get(i)._id;
        }
        plist += ")";
        Profile.delete(Profile.class, this, "_id IN " + plist, null);
    }

    // Clean up unnecessary cache data
    public void clean() {
        deleteProfiles(30);
        deleteUseless();
    }
}
