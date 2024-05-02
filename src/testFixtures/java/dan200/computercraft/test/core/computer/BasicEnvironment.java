/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.test.core.computer;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.filesystem.IWritableMount;
import dan200.computercraft.core.computer.ComputerEnvironment;
import dan200.computercraft.core.computer.GlobalEnvironment;
import dan200.computercraft.core.filesystem.FileMount;
import dan200.computercraft.core.filesystem.JarMount;
import dan200.computercraft.core.metrics.Metric;
import dan200.computercraft.core.metrics.MetricsObserver;
import dan200.computercraft.test.core.filesystem.MemoryMount;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * A basic implementation of {@link ComputerEnvironment} and {@link GlobalEnvironment}, suitable for a context which
 * will only run a single computer.
 */
public class BasicEnvironment implements ComputerEnvironment, GlobalEnvironment, MetricsObserver
{
    private final IWritableMount mount;

    public BasicEnvironment()
    {
        this( new MemoryMount() );
    }

    public BasicEnvironment( IWritableMount mount )
    {
        this.mount = mount;
    }

    @Override
    public IWritableMount createRootMount()
    {
        return mount;
    }

    @Override
    public int getDay()
    {
        return 0;
    }

    @Override
    public double getTimeOfDay()
    {
        return 0;
    }

    @Override
    public MetricsObserver getMetrics()
    {
        return this;
    }

    @Nonnull
    @Override
    public String getHostString()
    {
        return "ComputerCraft 1.0 (Test environment)";
    }

    @Nonnull
    @Override
    public String getUserAgent()
    {
        return "ComputerCraft/1.0";
    }

    @Override
    public IMount createResourceMount( String domain, String subPath )
    {
        return createMount( ComputerCraft.class, "data/" + domain + "/" + subPath, "main" );
    }

    @Override
    public InputStream createResourceFile( String domain, String subPath )
    {
        return ComputerCraft.class.getClassLoader().getResourceAsStream( "data/" + domain + "/" + subPath );
    }

    public static IMount createMount( Class<?> klass, String path, String fallback )
    {
        File file = getContainingFile( klass );

        if( file.isFile() )
        {
            try
            {
                return new JarMount( file, path );
            }
            catch( IOException e )
            {
                throw new UncheckedIOException( e );
            }
        }
        else
        {
            File wholeFile = new File( file, path );

            // If we don't exist, walk up the tree looking for resource folders
            File baseFile = file;
            while( baseFile != null && !wholeFile.exists() )
            {
                baseFile = baseFile.getParentFile();
                wholeFile = new File( baseFile, "src/" + fallback + "/resources/" + path );
            }

            if( !wholeFile.exists() ) throw new IllegalStateException( "Cannot find ROM mount at " + file );

            return new FileMount( wholeFile, 0 );
        }
    }


    private static File getContainingFile( Class<?> klass )
    {
        String path = klass.getProtectionDomain().getCodeSource().getLocation().getPath();
        int bangIndex = path.indexOf( "!" );

        // Plain old file, so step up from dan200.computercraft.
        if( bangIndex < 0 ) return new File( path );

        path = path.substring( 0, bangIndex );
        URL url;
        try
        {
            url = new URL( path );
        }
        catch( MalformedURLException e )
        {
            throw new IllegalStateException( e );
        }

        try
        {
            return new File( url.toURI() );
        }
        catch( URISyntaxException e )
        {
            return new File( url.getPath() );
        }
    }

    @Override
    public void observe( Metric.Counter counter )
    {
    }

    @Override
    public void observe( Metric.Event event, long value )
    {
    }
}
