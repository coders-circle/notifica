# -*- coding: utf-8 -*-
# Generated by Django 1.9 on 2016-01-02 02:56
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('classroom', '0007_subject_short_name'),
    ]

    operations = [
        migrations.AlterField(
            model_name='subject',
            name='name',
            field=models.CharField(max_length=50),
        ),
        migrations.AlterField(
            model_name='subject',
            name='short_name',
            field=models.CharField(max_length=5),
        ),
    ]