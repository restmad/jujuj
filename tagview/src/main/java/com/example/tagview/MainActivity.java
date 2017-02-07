package com.example.tagview;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;

import com.example.tagimageview.ITag;
import com.example.tagimageview.TagViewGroup;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

        final TagViewGroup tagView = (TagViewGroup) findViewById(R.id.tagview);
        Tag root = new Tag(1, "http://tagme-tagme.stor.sinaapp.com/imgs/20140926212439353.png", "hehe",
                new Point(400, 400), new Point(400, 400), ITag.Direction.LEFT);
        List<Tag> parents = new ArrayList<>();

        Tag parent1 = new Tag(2, "https://img3.doubanio.com/view/status/median/public/e4cf934c13c4385.jpg", "Hehe what th", new Point(200, 200), new Point(400, 400), ITag.Direction.LEFT);
        Tag child1 = new Tag(4, "", "Click me", new Point(100, 100), new Point(400, 400), ITag.Direction.LEFT);
        List<Tag> children = new ArrayList<>();
        children.add(child1);
        parent1.setTags(children);

        parents.add(parent1);
        parents.add(new Tag(3, "", "Keke", new Point(200, 200), new Point(400, 400), ITag.Direction.RIGHT));
        root.setTags(parents);

        tagView.setTag(root);
    }
}
