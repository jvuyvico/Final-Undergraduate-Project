# Generated by Django 3.2 on 2021-05-30 05:20

from django.db import migrations, models
import django.utils.timezone


class Migration(migrations.Migration):

    dependencies = [
        ('fromESP', '0004_auto_20210523_1937'),
    ]

    operations = [
        migrations.AddField(
            model_name='espattendance',
            name='dayStamp',
            field=models.DateField(auto_now_add=True, default=django.utils.timezone.now),
            preserve_default=False,
        ),
    ]
