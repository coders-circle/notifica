# -*- coding: utf-8 -*-
# Generated by Django 1.9 on 2016-02-23 13:25
from __future__ import unicode_literals

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('classroom', '0023_elective_p_class'),
    ]

    operations = [
        migrations.AlterField(
            model_name='elective',
            name='subject',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='classroom.Subject'),
        ),
    ]
