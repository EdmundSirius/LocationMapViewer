/*
 * Copyright (c) 2015 by k3b.
 *
 * This file is part of LocationMapViewer.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 */

package de.k3b.android.locationMapViewer.geobmp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import de.k3b.android.locationMapViewer.R;
import de.k3b.geo.api.IGeoInfoHandler;
import de.k3b.geo.api.IGeoPointInfo;

/**
 * Handles BookmarkList as part of the mapview
 * Created by k3b on 13.04.2015.
 */
public class BookmarkListOverlay implements IGeoInfoHandler {
    private final AdditionalPoints additionalPointProvider;

    public interface AdditionalPoints {
        GeoBmpDto[] getAdditionalPoints();
    }
    // private static final int MENU_ADD_CATEGORY = Menu.FIRST;
    private static final int EDIT_MENU_ID = Menu.FIRST + 137;

    protected final BookmarkListController bookMarkController;
    private final Activity context;

    private View fragmentBookmarkList;
    private DrawerLayout mDrawerLayout = null;
    private ImageButton cmdEdit     = null;
    private ImageButton cmdDelete   = null;
    private ImageButton cmdShowFavirites;
    private ImageButton cmdHideFavirites;

    private GeoBmpEditDialog edit = null;

    public BookmarkListOverlay(Activity context, AdditionalPoints additionalPointProvider) {
        this.additionalPointProvider = additionalPointProvider;
        bookMarkController = new BookmarkListController(context, (ListView) context.findViewById(android.R.id.list));
        bookMarkController.setSelChangedListener(new BookmarkListController.OnSelChangedListener() {
            @Override
            public void onSelChanged(GeoBmpDto newSelection) {
                BookmarkListOverlay.this.onSelChanged(newSelection);
            }
        });

        this.context = context;
        createButtons(context);
    }

    protected void onSelChanged(GeoBmpDto newSelection) {
        final boolean sel = (newSelection != null);
        cmdEdit.setEnabled(sel);
        cmdDelete.setEnabled(sel && BookmarkUtil.isBookmark(newSelection));
    }

    private void createButtons(Activity context) {
        mDrawerLayout = (DrawerLayout) context.findViewById(R.id.drawer_layout);
        fragmentBookmarkList = context.findViewById(R.id.fragment_bookmark_list);
        cmdShowFavirites = (ImageButton) context.findViewById(R.id.cmd_unhide_bookmark_list);
        cmdShowFavirites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFavorites(true);
            }
        });

        cmdHideFavirites = (ImageButton) context.findViewById(R.id.cmd_cancel);
        cmdHideFavirites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFavorites(false);
            }
        });

        showFavorites(false);
        cmdEdit = (ImageButton) context.findViewById(R.id.cmd_edit);
        cmdDelete = (ImageButton) context.findViewById(R.id.cmd_delete);

        cmdEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGeoPointEditDialog(bookMarkController.getCurrentItem());
            }
        });

        cmdDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteConfirm();
            }
        });
    }

    public BookmarkListOverlay showFavorites(boolean visible) {
        cmdShowFavirites.setVisibility((!visible) ? View.VISIBLE : View.INVISIBLE);
        // fragmentBookmarkList.setVisibility((visible) ? View.VISIBLE : View.INVISIBLE);
        if (visible) {
            if (additionalPointProvider != null) {
                this.bookMarkController.setAdditionalPoints(additionalPointProvider.getAdditionalPoints());
            }
            this.bookMarkController.reloadGuiFromRepository();
            mDrawerLayout.openDrawer(this.fragmentBookmarkList);
        } else {
            mDrawerLayout.closeDrawer(this.fragmentBookmarkList);
        }
        return this;
    }

    public void showGeoPointEditDialog(GeoBmpDto geoPointInfo) {
        if (this.edit == null) {
            this.edit = new GeoBmpEditDialog(this.context, this, R.layout.geobmp_edit_name);
            this.edit.setTitle(context.getString(R.string.title_bookmark_edit));
        }

        if (!BookmarkUtil.isBookmark(geoPointInfo)) {
            geoPointInfo = BookmarkUtil.createBookmark(geoPointInfo);
        }
        this.edit.onGeoInfo(geoPointInfo);
        this.context.showDialog(EDIT_MENU_ID);
    }

    public Dialog onCreateDialog(final int id) {
        switch (id) {
            case EDIT_MENU_ID:
                // case MENU_ADD_CATEGORY:
                return this.edit;
        }

        return null;
    }


    /** before deleting: "Are you shure?" */
    private void deleteConfirm() {
        final GeoBmpDto currentItem = this.bookMarkController.getCurrentItem();
        if (currentItem != null) {
            final String message = String.format(
                    this.context.getString(R.string.format_question_delete).toString(),
                    currentItem.getName() +"\n"+ currentItem.getSummary());

            final AlertDialog.Builder builder = new AlertDialog.Builder(this.context);

            builder.setTitle(R.string.title_confirm_delete);
            Bitmap bitmap = currentItem.getBitmap();

            if (bitmap != null) {
                BitmapDrawable drawable = (bitmap == null) ? null : new BitmapDrawable(context.getResources(), bitmap);
                builder.setIcon(drawable);
            }

            builder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.cmd_yes,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        final DialogInterface dialog,
                                        final int id) {
                                    bookMarkController.deleteCurrent();
                                }
                            }
                    )
                    .setNegativeButton(R.string.cmd_no,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        final DialogInterface dialog,
                                        final int id) {
                                    dialog.cancel();
                                }
                            }
                    );

            final AlertDialog alert = builder.create();
            alert.show();
        }
    }

    /**
     * Answer from edit via IGeoInfoHandler
     * @param geoInfo
     * @return true if item has been consumed
     */
    @Override
    public boolean onGeoInfo(IGeoPointInfo geoInfo) {
        this.bookMarkController.update(geoInfo);
        return true;
    }
}
