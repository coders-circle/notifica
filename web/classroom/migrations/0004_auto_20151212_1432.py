# -*- coding: utf-8 -*-
# Generated by Django 1.9 on 2015-12-12 14:32
from __future__ import unicode_literals

from django.conf import settings
from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('classroom', '0003_auto_20151212_1133'),
    ]

    operations = [
        migrations.DeleteModel(
            name='NotificaUniqueModel',
        ),
        migrations.AlterField(
            model_name='student',
            name='user',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to=settings.AUTH_USER_MODEL),
        ),
        migrations.AlterField(
            model_name='subject',
            name='department',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='classroom.Department'),
        ),
        migrations.AlterField(
            model_name='teacher',
            name='department',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='classroom.Department'),
        ),
        migrations.AlterField(
            model_name='teacher',
            name='user',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to=settings.AUTH_USER_MODEL),
        ),
    ]
